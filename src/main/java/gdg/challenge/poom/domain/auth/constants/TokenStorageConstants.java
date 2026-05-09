package gdg.challenge.poom.domain.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenStorageConstants {

    public static final String BLACKLIST_PREFIX = "BLACK:";
    public static final String REFRESH_TOKEN_PREFIX = "REFRESH:";
}
