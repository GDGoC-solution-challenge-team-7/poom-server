package gdg.challenge.poom.domain.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CharacterType {
    EMPATHY("ENFJ", "MELO"), SOLUTION("INTJ", "MARSH");

    private final String description;
    private final String name;
}
