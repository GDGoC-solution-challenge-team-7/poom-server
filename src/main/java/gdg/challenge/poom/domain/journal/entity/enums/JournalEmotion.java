package gdg.challenge.poom.domain.journal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JournalEmotion {
    LOVE("오늘은 사랑스러운 하루였어!"),
    HAPPY("오늘은 행복한 하루였어!"),
    PEACEFUL("오늘은 편안한 하루였어!"),
    NEUTRAL("오늘은 그저 그런 하루였어"),

    PAIN("오늘은 아픈 하루였어"),
    ANXIETY("오늘은 불안한 하루였어"),
    SAD("오늘은 슬픈 하루였어"),
    ANGER("오늘은 화가 나는 하루였어");

    private final String description;
}
