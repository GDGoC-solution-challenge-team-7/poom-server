package gdg.challenge.poom.domain.journal.service.query;

import gdg.challenge.poom.domain.journal.converter.JournalConverter;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import gdg.challenge.poom.domain.journal.repository.JournalRepository;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.JournalException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalQueryService {

    private final JournalRepository journalRepository;
    private final MemberRepository memberRepository;
    private final GcsService gcsService;

    public JournalResponseDTO.JournalDetail getJournal(Long memberId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_NOT_FOUND));

        if (journal.getMember().getId() != member.getId()){
            throw new JournalException(JournalErrorCode.JOURNAL_ACCESS_DENIED);
        }

        List<String> journalImageList = journal.getJournalImageList().stream()
                .map(JournalImage::getImageUrl)
                .map(gcsService::generateDownloadSignedUrl)
                .toList();

        return JournalConverter.toJournalDetail(journal, journalImageList);
    }

    public JournalResponseDTO.JournalList getCalendar(Long memberId, int year, int month){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        List<Journal> journalList = journalRepository.findByMemberAndJournalDateGreaterThanEqualAndJournalDateLessThan(member, start, end);

        return JournalConverter.toJournalList(journalList, year, month);
    }


}
