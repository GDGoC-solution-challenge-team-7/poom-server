package gdg.challenge.poom.domain.home.controller;

import gdg.challenge.poom.domain.home.dto.HomeResponseDTO;
import gdg.challenge.poom.global.error.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(name = "홈 화면 API")
public class HomeController {

    @Operation(summary = "출산 디데이 조회 API", description = "홈 화면에서 출산 디데이 조회하는 API")
    @GetMapping("/d-day")
    public ApiResponse<HomeResponseDTO.MemberBirthDate> getBirthDueDate(@RequestParam("userType") String userType){
        return ApiResponse.onSuccess(null);
    }
}
