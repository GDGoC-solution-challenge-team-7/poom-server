package gdg.challenge.poom.domain.home.service;

import gdg.challenge.poom.domain.home.converter.HomeConverter;
import gdg.challenge.poom.domain.home.dto.DateType;
import gdg.challenge.poom.domain.home.dto.HomeResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.Mother;
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
        Mother mother = null;

        if (userType == UserType.MOTHER){
            LocalDate childBirthDueDate = member.getChildBirthDate();
            if (today.isBefore(childBirthDueDate)) {
                mother = Mother.PREGNANT;
            } else {
                mother = Mother.POSTPARTUM;
            }

            between = ChronoUnit.DAYS.between(today, childBirthDueDate);
        } else {
            return HomeConverter.toMemberBirthDate(userType, null,null, null);
        }
        long absBetween = Math.abs(between);
        return calculateDateUnit(userType, mother, absBetween);
    }

    private HomeResponseDTO.MemberBirthDate calculateDateUnit(UserType userType, Mother mother, long between){
        // DateType.DAY
        if (between <= 30) {
            return HomeConverter.toMemberBirthDate(userType, mother, DateType.DAY, (int) between);
        }
        // DateType.MONTH
        if (between <= 30 * 12) {
            return HomeConverter.toMemberBirthDate(userType, mother, DateType.MONTH, (int) (between / 30));
        }
        // DateType.YEAR
        return HomeConverter.toMemberBirthDate(userType, mother, DateType.YEAR, (int) (between / (30 * 12)));
    }
}
