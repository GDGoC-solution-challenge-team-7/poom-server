package gdg.challenge.poom.global.util;

import gdg.challenge.poom.global.data.JwtConfigData;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    @Getter
    private final Duration accessExpiration;
    @Getter
    private final Duration refreshExpiration;

    public JwtUtil(JwtConfigData jwtConfigData) {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfigData.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(jwtConfigData.getTime().getAccessToken());
        this.refreshExpiration = Duration.ofMillis(jwtConfigData.getTime().getRefreshToken());
    }

    public String createAccessToken(CustomUserDetails details) {
        return createToken(details, accessExpiration);
    }

    public String createRefreshToken(CustomUserDetails details) {
        return createToken(details, refreshExpiration);
    }

    public Long getMemberId(String token) {
        try {
            return getClaims(token).getPayload().get("id", Long.class);
        } catch (ExpiredJwtException e) {
//            throw new TokenException(TokenErrorCode.TOKEN_EXPIRED);
            return null;
        } catch (JwtException e) {
            return null;
        }
    }

    public String getRole(String token) {
        try {
            return getClaims(token).getPayload().get("role", String.class);
        } catch (ExpiredJwtException e) {
//            throw new TokenException(TokenErrorCode.TOKEN_EXPIRED);
            return null;
        } catch (JwtException e) {
            return null;
        }
    }

    private String createToken(CustomUserDetails detail, Duration expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(detail.getUsername())
                .claim("id", detail.getMemberId())
                .claim("role", detail.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey)
                .compact();
    }

    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }



}
