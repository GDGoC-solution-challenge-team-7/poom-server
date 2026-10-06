package gdg.challenge.poom.domain.auth.service.event;

import gdg.challenge.poom.domain.auth.dto.util.EventDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.util.CoupleCodeGenerator;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CoupleCodeEventHandler {
    private final MemberRepository memberRepository;
    private final CoupleCodeGenerator coupleCodeGenerator;

    @EventListener
    @Transactional
    public void handle(EventDTO.SignUpCompletedEvent event){
        Member member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getCoupleCode() != null) return;
        String code;
        do {
            code = coupleCodeGenerator.generate();
        } while (memberRepository.existsByCoupleCode(code));

        member.assignCoupleCode(code);
    }
}
