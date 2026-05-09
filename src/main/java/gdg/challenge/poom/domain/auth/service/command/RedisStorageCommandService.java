package gdg.challenge.poom.domain.auth.service.command;

import gdg.challenge.poom.domain.auth.constants.TokenStorageConstants;
import gdg.challenge.poom.domain.auth.service.query.TokenQueryService;
import gdg.challenge.poom.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisStorageCommandService {

    private final RedisUtil redisUtil;
    private final TokenQueryService tokenQueryService;

    public void addRefreshToken(Long memberId, String refreshToken) {
        redisUtil.save(
                TokenStorageConstants.REFRESH_TOKEN_PREFIX + memberId, refreshToken, tokenQueryService.getRefreshTokenExpiration()
        );
    }

    public void addBlackList(String token) {
        redisUtil.save(
                TokenStorageConstants.BLACKLIST_PREFIX + token, true, tokenQueryService.getRefreshTokenExpiration()
        );
    }

    public void deleteRefreshToken(Long memberId){
        redisUtil.delete(TokenStorageConstants.REFRESH_TOKEN_PREFIX + memberId);
    }
}
