package gdg.challenge.poom.domain.journal.converter;

import gdg.challenge.poom.domain.journal.dto.request.JournalRequestDTO;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class JournalConverter {

    public static Journal toJournal(JournalRequestDTO.JournalRequest request){
        return Journal.builder()
                .journalDate(request.journalDate())
                .journalEmotion(request.journalEmotion())
                .content(request.content())
                .visibility(request.visibility())
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

    public static JournalResponseDTO.JournalDetail toJournalDetail(Journal journal, List<JournalResponseDTO.ImageUrl> signedUrlList){
        JournalResponseDTO.Author author = JournalResponseDTO.Author.builder()
                .memberId(journal.getMember().getId())
                .nickname(journal.getMember().getName())
                .build();

        return JournalResponseDTO.JournalDetail.builder()
                .journalId(journal.getId())
                .author(author)
                .journalEmotion(journal.getJournalEmotion())
                .journalDescription(journal.getJournalEmotion().getDescription())
                .content(journal.getContent())
                .visibility(journal.getVisibility())
                .imageUrls(signedUrlList)
                .build();
    }

    public static JournalResponseDTO.JournalByDate toJournalByDate(
            LocalDate journalDate, Journal myJournal, List<JournalResponseDTO.ImageUrl> myUrlList,
            Journal partnerJournal, List<JournalResponseDTO.ImageUrl> partnerUrlList
    ){
        return JournalResponseDTO.JournalByDate.builder()
                .journalDate(journalDate)
                .myJournal(toJournalDetail(myJournal, myUrlList))
                .partnerJournal(toJournalDetail(partnerJournal, partnerUrlList))
                .build();
    }

    // 리스트의 객체
    public static JournalResponseDTO.Journal toJournal(Journal journal){
        return JournalResponseDTO.Journal.builder()
                .journalId(journal.getId())
                .journalDate(journal.getJournalDate())
                .journalEmotion(journal.getJournalEmotion())
                .build();
    }

    public static JournalResponseDTO.JournalListByMonth toJournalList(List<Journal> journalList, YearMonth month){

        List<JournalResponseDTO.Journal> list = journalList.stream()
                .map(JournalConverter::toJournal)
                .toList();


        return JournalResponseDTO.JournalListByMonth.builder()
                .month(month)
                .journalList(list)
                .build();
    }

    public static JournalResponseDTO.JournalChangedVisibility toJournalChangedVisibility(Long journalId, Visibility visibility){
        return JournalResponseDTO.JournalChangedVisibility.builder()
                .journalId(journalId)
                .visibility(visibility)
                .build();
    }

}
