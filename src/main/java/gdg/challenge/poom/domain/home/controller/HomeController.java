package gdg.challenge.poom.domain.home.controller;

import gdg.challenge.poom.global.error.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    @Operation(summary = "출산 디데이 조회 API", description = "회원가입하는 API")
    @GetMapping("/d-day")
    public ApiResponse<Void> getBirthDueDate(@RequestParam("userType") String userType){
        return ApiResponse.onSuccess(null);
    }
}
