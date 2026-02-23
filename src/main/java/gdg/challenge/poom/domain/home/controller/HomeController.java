package gdg.challenge.poom.domain.home.controller;

import gdg.challenge.poom.domain.home.dto.HomeResponseDTO;
import gdg.challenge.poom.domain.home.service.HomeService;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(name = "홈 화면 API")
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "출산 디데이 조회 API", description = "홈 화면에서 출산 디데이 조회하는 API")
    @GetMapping("/d-day")
    public ApiResponse<HomeResponseDTO.MemberBirthDate> getBirthDueDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        HomeResponseDTO.MemberBirthDate birthDueDate = homeService.getBirthDueDate(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(birthDueDate);
    }
}
