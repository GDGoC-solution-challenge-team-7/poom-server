package gdg.challenge.poom.domain.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CharacterType {
    EMPATHY("ENFJ", "MELO", "멜로"), SOLUTION("INTJ", "MARSH", "마쉬");

    private final String description;
    private final String name;
    private final String koName;
}
