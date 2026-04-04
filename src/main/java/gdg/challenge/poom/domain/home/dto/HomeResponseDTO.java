package gdg.challenge.poom.domain.home.dto;

import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.Mother;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import lombok.Builder;

public record HomeResponseDTO (){

    @Builder
    public record MemberBirthDate(
            UserType userType,
            Mother mother,
            BirthRelationship birthRelationship,
            DateType dateType,
            Integer number
    ){}
}
