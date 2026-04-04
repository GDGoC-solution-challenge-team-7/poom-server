package gdg.challenge.poom.domain.util;

import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.Mother;

import java.time.LocalDate;

public class MemberStatusUtil {

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
