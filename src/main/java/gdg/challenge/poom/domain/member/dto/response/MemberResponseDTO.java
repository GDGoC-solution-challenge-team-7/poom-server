package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.member.entity.enums.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record MemberResponseDTO() {

    @Builder
    public record MemberInfo(
            String name,
            String email,
            LocalDate birthDate,
            Gender gender,
            UserType userType,
            Mother mother,
            LocalDate childBirthDate,
            String profileImageSignedUrl,
            String socialType
    ){}

    // 업로드된 파일 응답
    @Builder
    public record SignedUrlResponse(
            String objectName,
            String signedUrl,
            String publicUrl
    ){ }

    // 업로드된 파일 요청 - 여러건
    @Builder
    public record SignedUrlBatchResponse(
            List<MemberResponseDTO.SignedUrlResponse> files
    ){}

    @Builder
    public record CoupleConnectionInfo(
            Long coupleId,
            Long memberId,
            String memberNickname,
            Long partnerId,
            String partnerNickname,
            CoupleStatus coupleStatus,
            String coupleCode
    ){}
}
