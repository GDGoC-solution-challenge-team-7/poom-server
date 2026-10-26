package gdg.challenge.poom.domain.member.controller;

import gdg.challenge.poom.domain.member.dto.request.CoupleRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.CoupleResponseDTO;
import gdg.challenge.poom.domain.member.service.CoupleCommandService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/couples")
@Tag(name = "부부 API")
public class CoupleController {

    private final CoupleCommandService coupleCommandService;

    @Operation(summary = "부부 연결 전 상대방 미리 확인", description = "연결 코드로 상대방 이름만 조회하는 API, Couple을 생성하거나 연결 상태를 변경하지 않음")
    @PostMapping("/invitations/preview")
    public ApiResponse<CoupleResponseDTO.PartnerPreview> preview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CoupleRequestDTO.CoupleCodePreview request
    ){
        CoupleResponseDTO.PartnerPreview partnerPreview = coupleCommandService.previewCouple(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(partnerPreview);
    }

    @Operation(summary = "코드 입력 후 부부 연결", description = "코드 입력을 통해 부부 연결하는 API")
    @PostMapping("/invitations/join")
    public ApiResponse<CoupleResponseDTO.CreatedCouple> join(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CoupleRequestDTO.CoupleCode request
    ){
        CoupleResponseDTO.CreatedCouple createdCouple = coupleCommandService.connectCouple(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(createdCouple);
    }

    @Operation(summary = "부부 연결 복구", description = "부부 연결 복구하는 API")
    @PostMapping("/rejoin")
    public ApiResponse<Void> rejoin(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        coupleCommandService.rejoinCouple(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "부부 연결 해제 - 30일 유예", description = "부부 연결 해제하는 API")
    @PatchMapping
    public ApiResponse<CoupleResponseDTO.ChangeStatusCouple> deleteConnectionByPatch(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CoupleResponseDTO.ChangeStatusCouple changeStatusCouple = coupleCommandService.deleteCoupleByPatch(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(changeStatusCouple);
    }

    @Operation(summary = "부부 영구 연결 해제", description = "부부 영구 연결 해제하는 API, 복구 불가")
    @DeleteMapping
    public ApiResponse<CoupleResponseDTO.DeletedCouple> deleteConnection(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CoupleResponseDTO.DeletedCouple deletedCouple = coupleCommandService.deleteCouple(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(deletedCouple);
    }
}
