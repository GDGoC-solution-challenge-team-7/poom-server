package gdg.challenge.poom.domain.auth.service.query;

import gdg.challenge.poom.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenQueryService {

    private final JwtUtil jwtUtil;

    public Long getMemberId(String token) {
        return jwtUtil.getMemberId(token);
    }

    public Duration getAccessTokenExpiration() {
        return jwtUtil.getAccessExpiration();
    }

    public Duration getRefreshTokenExpiration() {
        return jwtUtil.getRefreshExpiration();
    }
}
