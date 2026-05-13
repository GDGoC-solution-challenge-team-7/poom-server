package gdg.challenge.poom.domain.auth.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithdrawalReason {
    TOO_DIFFICULT("It's too difficult to use"),          // 사용하기 어려움
    BUGS("I keep running into bugs"),
    NOT_HELPFUL("It's not helpful for me"),
    FOUND_OTHER_SERVICE("I found another service to use"),
    MISSING_FEATURES("It's missing the features I expected"),
    OTHER("Other")
    ;

    private final String description;
}
