package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import gdg.challenge.poom.domain.journal.repository.JournalRepository;
import gdg.challenge.poom.domain.member.converter.CoupleConverter;
import gdg.challenge.poom.domain.member.dto.request.CoupleRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.CoupleResponseDTO;
import gdg.challenge.poom.domain.member.entity.Couple;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.CoupleStatus;
import gdg.challenge.poom.domain.member.repository.CoupleRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.CoupleErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.CoupleException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CoupleCommandService {
    private final MemberRepository memberRepository;
    private final CoupleRepository coupleRepository;
    private final JournalRepository journalRepository;

    // 코드 입력 후 연결
    public CoupleResponseDTO.CreatedCouple connectCouple(Long memberId, CoupleRequestDTO.CoupleCode request) {
        validateCoupleConnect(memberId);

        // codeSubmitter, memberB: 연결 코드를 입력한 사람
        Member codeSubmitter = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // codeOwner, memberA: 연결 코드 주인
        Member codeOwner = memberRepository.findByCoupleCode(request.coupleCode())
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.MEMBER_NOT_FOUND_BY_CODE));

        // 자신과 연결 될 수 없음
        if (codeSubmitter.getId().equals(codeOwner.getId())) {
            throw new CoupleException(CoupleErrorCode.SAME_MEMBER_CONNECTION_NOT_ALLOWED);
        }

        Couple saved = coupleRepository.save(Couple.create(codeSubmitter, codeOwner));
        return CoupleConverter.toCreatedCouple(saved);
    }

    // 코드 입력 전, 상대방 정보 미리 확인 (Couple 생성/상태 변경 없음)
    @Transactional(readOnly = true)
    public CoupleResponseDTO.PartnerPreview previewCouple(Long memberId, CoupleRequestDTO.CoupleCodePreview request) {
        // codeOwner: 연결 코드 주인
        Member codeOwner = memberRepository.findByCoupleCode(request.code())
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.MEMBER_NOT_FOUND_BY_CODE));

        // 자신의 코드는 아닌지 확인
        if (codeOwner.getId().equals(memberId)) {
            throw new CoupleException(CoupleErrorCode.SAME_MEMBER_CONNECTION_NOT_ALLOWED);
        }

        // 두 사람 모두 연결 가능한 상태인지 확인
        validateCoupleConnect(memberId);
        validateCoupleConnect(codeOwner.getId());

        return CoupleConverter.toPartnerPreview(codeOwner);
    }

    // 부부 연결 복구
    public void rejoinCouple(Long memberId) {
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        Member partner = couple.getPartnerOf(memberId);
        couple.restore();
        journalRepository.restoreVisibility(List.of(memberId, partner.getId()));
    }

    // 부부 연결 해제 - 유예 시간 30일, 30일 후 영구 삭제
    public CoupleResponseDTO.ChangeStatusCouple deleteCoupleByPatch(Long memberId) {
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        Member partner = couple.getPartnerOf(memberId);
        couple.requestDisconnect();
        journalRepository.backupAndMakePrivate(List.of(memberId, partner.getId()),Visibility.PRIVATE);
        return CoupleConverter.toChangeStatusCouple(partner.getId(), memberId, couple);
    }

    // 부부 연결 영구 해제
    public CoupleResponseDTO.DeletedCouple deleteCouple(Long memberId) {
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        Member partner = couple.getPartnerOf(memberId);
        journalRepository.makeAllPrivateAndClearBackup(List.of(memberId), Visibility.PRIVATE);
        coupleRepository.delete(couple);
        return CoupleConverter.toDeletedCouple(partner.getId(), memberId);
    }

    // 만료 시 부부 데이터 삭제
    public void deleteExpiredConnections(){
        List<Couple> expiredCouple = coupleRepository.findByCoupleStatusAndDeleteScheduledAtBefore(
                CoupleStatus.DISCONNECTED_GRACE_PERIOD, LocalDateTime.now()
        );
        coupleRepository.deleteAll(expiredCouple);
    }

    private void validateCoupleConnect(Long memberId){
        // 기존 부부 연결이 완전히 끊어져야 가능
        if (coupleRepository.existsByMemberIdAndStatus(memberId, List.of(CoupleStatus.CONNECTED))) {
            throw new CoupleException(CoupleErrorCode.MEMBER_ALREADY_CONNECTED);
        }
        // 유예 기간에 있는 부부 연결이 완전히 끊어져야 가능
        if (coupleRepository.existsByMemberIdAndStatus(memberId, List.of(CoupleStatus.DISCONNECTED_GRACE_PERIOD))) {
            throw new CoupleException(CoupleErrorCode.NEW_CONNECTION_NOT_ALLOWED_DURING_REJOIN_PERIOD);
        }
    }
}
