package gdg.challenge.poom.domain.journal.converter;

import gdg.challenge.poom.domain.journal.dto.request.JournalRequestDTO;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;

import java.time.LocalDate;
import java.util.List;

public class JournalConverter {

    public static Journal toJournal(JournalRequestDTO.JournalRequest request){
        return Journal.builder()
                .journalDate(request.journalDate())
                .journalEmotion(request.journalEmotion())
                .content(request.content())
                .build();

    }

    public static List<JournalImage> toJournalImage (List<String> imageUrls){
        return imageUrls.stream()
                .map(imageUrl ->
                            JournalImage.builder()
                                    .imageUrl(imageUrl)
                                    .build()
                ).toList();
    }

    public static JournalResponseDTO.CreatedJournal toCreatedJournal(LocalDate journalDate, JournalEmotion journalEmotion){
        return JournalResponseDTO.CreatedJournal.builder()
                .journalDate(journalDate)
                .journalEmotion(journalEmotion)
                .build();
    }

    public static JournalResponseDTO.JournalDetail toJournalDetail(Journal journal){

        List<String> journalImageList = journal.getJournalImageList().stream()
                .map(JournalImage::getImageUrl)
                .toList();


        return JournalResponseDTO.JournalDetail.builder()
                .journalDate(journal.getJournalDate())
                .journalEmotion(journal.getJournalEmotion())
                .content(journal.getContent())
                .imageUrls(journalImageList)
                .build();
    }

    public static JournalResponseDTO.Journal toJournal(Journal journal){
        return JournalResponseDTO.Journal.builder()
                .journalId(journal.getId())
                .journalDate(journal.getJournalDate())
                .journalEmotion(journal.getJournalEmotion())
                .build();
    }

    public static JournalResponseDTO.JournalList toJournalList(List<Journal> journalList, int year, int month){

        List<JournalResponseDTO.Journal> list = journalList.stream()
                .map(JournalConverter::toJournal)
                .toList();


        return JournalResponseDTO.JournalList.builder()
                .year(year)
                .month(month)
                .journalList(list)
                .build();
    }

}
