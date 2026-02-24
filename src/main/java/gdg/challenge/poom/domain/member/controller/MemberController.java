package gdg.challenge.poom.domain.member.controller;

import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.service.MemberCommandService;
import gdg.challenge.poom.domain.member.service.MemberQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

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


}
