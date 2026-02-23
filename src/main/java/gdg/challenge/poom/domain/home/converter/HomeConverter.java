package gdg.challenge.poom.domain.home.converter;

import gdg.challenge.poom.domain.home.dto.DateType;
import gdg.challenge.poom.domain.home.dto.HomeResponseDTO;
import gdg.challenge.poom.domain.member.entity.enums.UserType;

public class HomeConverter {

    public static HomeResponseDTO.MemberBirthDate toMemberBirthDate(UserType userType, DateType dateType, Integer number) {
        return HomeResponseDTO.MemberBirthDate.builder()
                .userType(userType)
                .dateType(dateType)
                .number(number)
                .build();
    }
}
