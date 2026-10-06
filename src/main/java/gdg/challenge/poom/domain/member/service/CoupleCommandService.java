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

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CoupleCommandService {
    private final MemberRepository memberRepository;
    private final CoupleRepository coupleRepository;

    // 연결 코드 생성
    public String createCoupleCode() {
        return null;
    }

    // 코드 입력 후 연결
    public CoupleResponseDTO.CreatedCouple connectCouple(Long memberId, CoupleRequestDTO.CoupleCode request) {
        // codeSubmitter, memberB: 연결 코드를 입력한 사람
        Member codeSubmitter = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // codeOwner, memberA: 연결 코드 주인
        Member codeOwner = memberRepository.findByCoupleCode(request.coupleCode())
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.MEMBER_NOT_FOUND_BY_CODE));
        Couple couple = CoupleConverter.createCouple(codeSubmitter, codeOwner);
        Couple saved = coupleRepository.save(couple);
        return CoupleConverter.toCreatedCouple(saved);
    }

    // 부부 연결 복구
    public String rejoinCouple(Long memberId) {

        return null;
    }

    // 부부 연결 해제
    public CoupleResponseDTO.ChangeStatusCouple deleteCouple(Long memberId) {
        Couple couple = coupleRepository.findByMemberA_IdOrMemberB_Id(memberId, memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));
        couple.changeCoupleStatus(CoupleStatus.DISCONNECTED_GRACE_PERIOD);
        Member partner = couple.getPartnerOf(memberId);
        return CoupleConverter.toChangeStatusCouple(partner.getId(), memberId, couple);
    }
}
