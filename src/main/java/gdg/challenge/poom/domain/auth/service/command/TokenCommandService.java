package gdg.challenge.poom.domain.auth.service.command;

import gdg.challenge.poom.domain.auth.converter.AuthConverter;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import gdg.challenge.poom.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCommandService {

    private final JwtUtil jwtUtil;

    public AuthResponseDTO.TokenResult createLoginToken(CustomUserDetails customUserDetails) {
        return AuthConverter.toTokenResult(
                customUserDetails.getMemberId(),
                jwtUtil.createAccessToken(customUserDetails),
                jwtUtil.createRefreshToken(customUserDetails)
        );
    }

    public String reissueAccessToken(CustomUserDetails customUserDetails) {
        return jwtUtil.createAccessToken(customUserDetails);
    }
}
