package gdg.challenge.poom.domain.journal.dto.response;

import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import lombok.Builder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record JournalResponseDTO() {
    @Builder
    public record CreatedJournal(
            LocalDate journalDate,
            JournalEmotion journalEmotion,
            Visibility visibility
    ){}

    @Builder
    public record JournalDetail(
            Long journalId,
            LocalDate journalCreatedDate,
            Author author,
            JournalEmotion journalEmotion,
            String journalDescription,
            String content,
            Visibility visibility,
            List<ImageUrl> imageUrls
    ){}

    @Builder
    public record JournalByDate(
            LocalDate journalDate,
            JournalDetail myJournal,
            JournalDetail partnerJournal
    ){}

    @Builder
    public record JournalList(
            YearMonth journalDate,
            List<JournalDetail> journalDetails
    ){}

    // 달력 조회용 리스트
    @Builder
    public record JournalListByMonth(
            YearMonth month,
            List<Journal> journalList
    ){}

    @Builder
    public record Journal(
            Long journalId,
            LocalDate journalDate,
            JournalEmotion journalEmotion
    ){}

    @Builder
    public record JournalChangedVisibility(
            Long journalId,
            Visibility visibility
    ){}

    @Builder
    public record Author(
            Long memberId,
            String nickname
    ){}

    @Builder
    public record ImageUrl(
            Long imageId,
            String url
    ){}
}
