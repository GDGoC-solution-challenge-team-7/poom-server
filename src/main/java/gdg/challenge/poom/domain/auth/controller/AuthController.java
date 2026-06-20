package gdg.challenge.poom.domain.auth.controller;

import gdg.challenge.poom.domain.auth.dto.request.AuthRequestDTO;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.auth.service.command.AuthCommandService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "인증 API")
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "구글 소셜 로그인 API", description = "구글 소셜 로그인하는 API")
    @GetMapping("/callback")
    public ApiResponse<OAuth2ResponseDTO.Login> signUp(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam String code){
        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        log.info("code(raw)='{}' len={}", code, code.length());
        log.info("code(decoded)='{}'", decodedCode);
        OAuth2ResponseDTO.Login login = authCommandService.loginWithOAuth(request, response, decodedCode);
        return ApiResponse.onSuccess(login);
    }

    @PostMapping("/google")
    public ApiResponse<OAuth2ResponseDTO.Login> googleLogin(AuthRequestDTO.TokenRequest tokenRequest) {
        OAuth2ResponseDTO.Login login = authCommandService.verifyGoogleIdToken(tokenRequest.idToken());
        return ApiResponse.onSuccess(login);
    }

    @Operation(summary = "회원가입 API", description = "회원가입하는 API")
    @PostMapping("/sign-up")
    public ApiResponse<AuthResponseDTO.TokenResult> signUp(@RequestBody @Valid AuthRequestDTO.SignUp request){
        AuthResponseDTO.TokenResult tokenResult = authCommandService.signUp(request);
        return ApiResponse.onSuccess(tokenResult);
    }

    @Operation(summary = "Access Token 재발급 API", description = "토큰 재발급 API")
    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.AccessTokenResult> reissue(HttpServletRequest request, HttpServletResponse response){
        AuthResponseDTO.AccessTokenResult accessTokenResult = authCommandService.reissue(request, response);
        return ApiResponse.onSuccess(accessTokenResult);
    }
}
