package gdg.challenge.poom.domain.home.service;

import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Transactional
public class HomeService {

    private final MemberRepository memberRepository;

    public void getBirthDueDate(UserType userType, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (userType == UserType.PREGNANT){
//            member.getChildBirthDueDate() - LocalDate.now();
        } else if (userType == UserType.POSTPARTUM){

        } else {

        }
    }
}
