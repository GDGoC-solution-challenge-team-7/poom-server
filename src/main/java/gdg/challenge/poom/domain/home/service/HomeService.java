package gdg.challenge.poom.domain.home.service;

import gdg.challenge.poom.domain.home.converter.HomeConverter;
import gdg.challenge.poom.domain.home.dto.DateType;
import gdg.challenge.poom.domain.home.dto.HomeResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class HomeService {

    private final MemberRepository memberRepository;

    public HomeResponseDTO.MemberBirthDate getBirthDueDate(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        UserType userType = member.getUserType();
        LocalDate today = LocalDate.now();
        long between;

        if (userType == UserType.PREGNANT){
            LocalDate childBirthDueDate = member.getChildBirthDueDate();
            between = ChronoUnit.DAYS.between(today, childBirthDueDate);
        } else if (userType == UserType.POSTPARTUM){
            LocalDate childBirthDate = member.getChildBirthDate();
            between = ChronoUnit.DAYS.between(today, childBirthDate);
        } else {
            return HomeConverter.toMemberBirthDate(userType, null, null);
        }
        return calculateDateUnit(userType, between);
    }

    private HomeResponseDTO.MemberBirthDate calculateDateUnit(UserType userType, long between){
        // DateType.DAY
        if (between <= 30) {
            return HomeConverter.toMemberBirthDate(userType, DateType.DAY, (int) between);
        }
        // DateType.MONTH
        if (between <= 30 * 12) {
            return HomeConverter.toMemberBirthDate(userType, DateType.MONTH, (int) (between / 30));
        }
        // DateType.YEAR
        return HomeConverter.toMemberBirthDate(userType, DateType.YEAR, (int) (between / (30 * 12)));
    }
}
