package gdg.challenge.poom.domain.auth.service.query;

import gdg.challenge.poom.domain.auth.constants.TokenStorageConstants;
import gdg.challenge.poom.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisStorageQueryService {
    private final RedisUtil redisUtil;
    private final TokenQueryService tokenQueryService;

    public boolean isBlackList(String token) {
        return Boolean.TRUE.equals(redisUtil.has(TokenStorageConstants.BLACKLIST_PREFIX + token));
    }

    public String getRefreshToken(Long memberId) {
        return redisUtil.get(TokenStorageConstants.REFRESH_TOKEN_PREFIX + memberId, String.class);
    }
}
