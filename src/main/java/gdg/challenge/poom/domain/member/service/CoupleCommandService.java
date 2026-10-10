package gdg.challenge.poom.domain.member.service;

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

    // 부부 연결 복구
    public void rejoinCouple(Long memberId) {
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        couple.restore();
    }

    // 부부 연결 해제 - 유예 시간 30일, 30일 후 영구 삭제
    public CoupleResponseDTO.ChangeStatusCouple deleteCouple(Long memberId) {
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        couple.requestDisconnect();
        Member partner = couple.getPartnerOf(memberId);
        return CoupleConverter.toChangeStatusCouple(partner.getId(), memberId, couple);
    }

    // 만료 시 부부 데이터 삭제
    public void deleteExpiredConnections(){
        List<Couple> expiredCouple = coupleRepository.findByCoupleStatusAndDeleteScheduledAtBefore(
                CoupleStatus.DISCONNECTED_GRACE_PERIOD, LocalDateTime.now()
        );

        for (Couple c : expiredCouple) {
            // TODO: 연관 데이터부터 삭제 후 연결 삭제
            coupleRepository.delete(c);
        }
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
