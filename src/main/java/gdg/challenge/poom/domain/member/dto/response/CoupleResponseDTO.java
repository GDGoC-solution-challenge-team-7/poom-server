package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.member.entity.enums.CoupleStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record CoupleResponseDTO() {

    @Builder
    public record CreatedCouple(
        Long coupleId,
        Long codeSubmitterId,
        Long codeOwnerId,
        String codeSubmitterNickname,
        String codeOwnerNickname,
        LocalDateTime createdAt
    ){}

    @Builder
    public record ChangeStatusCouple(
        Long memberId,
        Long partnerId,
        CoupleStatus coupleStatus
    ){}
}
