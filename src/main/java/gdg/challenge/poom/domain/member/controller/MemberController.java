package gdg.challenge.poom.domain.member.controller;

import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.domain.member.service.MemberCommandService;
import gdg.challenge.poom.domain.member.service.MemberQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
@Tag(name = "회원 API")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final GcsService gcsService;

    @Operation(summary = "멤버 정보 조회 API", description = "멤버의 정보를 조회하는 API")
    @GetMapping
    public ApiResponse<MemberResponseDTO.MemberInfo> getMemberInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        MemberResponseDTO.MemberInfo memberInfo = memberQueryService.getMemberInfo(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(memberInfo);
    }

    @Operation(summary = "멤버 정보 수정 API", description = "멤버의 정보를 수정하는 API")
    @PutMapping
    public ApiResponse<Void> changeMemberInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberRequestDTO.ChangeMemberInfo request
    ){
        memberCommandService.changeMemberInfo(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "이미지 업로드용 Signed URL 발급",
            description = "해당 API를 호출하여 업로드 URL을 받은 뒤, 해당 URL로 파일 바이너리를 포함한 PUT 요청을 전송하여 업로드를 수행" +
                    "목적별 업로드 도메인 선택(PROFILE_IMAGE|CHAT_IMAGE|EXPERT_VERIFICATION)"
    )
    @PostMapping("signed-url")
    public ApiResponse<MemberResponseDTO.SignedUrlBatchResponse> createSignedUrl(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberRequestDTO.SignedUrlBatchRequest request
    ){
        return ApiResponse.onSuccess(gcsService.generateUploadSignedUrl(customUserDetails.getMemberId(), request));
    }

    @Operation(summary = "사용자 프로필 이미지 업로드",
            description = "발급받은 signed-url로 사용자 profileImage 저장하는 API" +
                    " objectName 요청")
    @PostMapping("profile-images")
    public ApiResponse<Void> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberRequestDTO.ProfileImageRequest request
    ){
        memberCommandService.uploadMemberProfileImage(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(null);
    }
}
