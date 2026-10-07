package gdg.challenge.poom.domain.member.controller;

import gdg.challenge.poom.domain.member.dto.request.CoupleRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.CoupleResponseDTO;
import gdg.challenge.poom.domain.member.service.CoupleCommandService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
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

    @PostMapping("/invitations/join")
    public ApiResponse<CoupleResponseDTO.CreatedCouple> join(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CoupleRequestDTO.CoupleCode request
    ){
        CoupleResponseDTO.CreatedCouple createdCouple = coupleCommandService.connectCouple(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(createdCouple);
    }

    @PostMapping("/rejoin")
    public ApiResponse<Void> rejoin(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        coupleCommandService.rejoinCouple(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping
    public ApiResponse<CoupleResponseDTO.ChangeStatusCouple> deleteConnection(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CoupleResponseDTO.ChangeStatusCouple changeStatusCouple = coupleCommandService.deleteCouple(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(changeStatusCouple);
    }
}
