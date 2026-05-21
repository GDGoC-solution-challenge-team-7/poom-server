package gdg.challenge.poom.domain.journal.dto.response;

import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record JournalResponseDTO() {
    @Builder
    public record CreatedJournal(
            LocalDate journalDate,
            JournalEmotion journalEmotion
    ){}

    @Builder
    public record JournalDetail(
            LocalDate journalDate,
            JournalEmotion journalEmotion,
            String content,
            List<String> imageUrls
    ){}

    @Builder
    public record JournalList(
            int year,
            int month,
            List<Journal> journalList
    ){}

    @Builder
    public record Journal(
            LocalDate journalDate,
            JournalEmotion journalEmotion
    ){}
}
