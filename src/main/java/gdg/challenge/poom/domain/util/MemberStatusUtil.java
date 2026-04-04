package gdg.challenge.poom.domain.util;

import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.Mother;

import java.time.LocalDate;

public class MemberStatusUtil {

    // 현재의 날짜와 비교해 출산 상태(Mother -> PREGNANT or POSTPARTUM)를 반환
    public static Mother calcMotherStatus(Member member){
        LocalDate today = LocalDate.now();
        LocalDate childBirthDueDate = member.getChildBirthDate();
        if (today.isBefore(childBirthDueDate)) {
            return Mother.PREGNANT;
        } else {
            return Mother.POSTPARTUM;
        }
    }
}
