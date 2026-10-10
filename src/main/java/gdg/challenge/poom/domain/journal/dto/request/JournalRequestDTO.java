package gdg.challenge.poom.domain.journal.dto.request;

import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record JournalRequestDTO() {

    public record JournalRequest(
            LocalDate journalDate,
            JournalEmotion journalEmotion,
            String content,
            List<String> imageUrls,
            Visibility visibility
    ){}

    public record JournalVisibilityRequest(
            @NotNull
            Visibility visibility
    ){}
}
