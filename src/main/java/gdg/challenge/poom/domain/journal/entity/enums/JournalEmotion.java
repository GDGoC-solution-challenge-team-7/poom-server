package gdg.challenge.poom.domain.journal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JournalEmotion {
    LOVE("사랑"),
    HAPPY("행복"),
    PEACEFUL("편안"),
    NEUTRAL("무난"),

    PAIN("아픔"),
    ANXIETY("불안"),
    SAD("슬픔"),
    ANGER("화남");

    private final String description;
}
