package gdg.challenge.poom.domain.chat.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CharacterType {
    EMPATHY("ENFJ"), SOLUTION("INTJ");

    private final String description;
}
