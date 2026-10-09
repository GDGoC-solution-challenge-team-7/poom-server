package gdg.challenge.poom.domain.journal.service.query;

import gdg.challenge.poom.domain.journal.converter.JournalConverter;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.repository.JournalRepository;
import gdg.challenge.poom.domain.member.entity.Couple;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.CoupleRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.global.error.code.status.CoupleErrorCode;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.CoupleException;
import gdg.challenge.poom.global.error.exception.handler.JournalException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalQueryService {

    private final JournalRepository journalRepository;
    private final MemberRepository memberRepository;
    private final CoupleRepository coupleRepository;
    private final GcsService gcsService;

    public JournalResponseDTO.JournalDetail getJournal(Long memberId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_NOT_FOUND));

        if (journal.getMember().getId() != member.getId()){
            throw new JournalException(JournalErrorCode.JOURNAL_ACCESS_DENIED);
        }

        List<JournalResponseDTO.ImageUrl> journalImageList = journal.getJournalImageList().stream()
                .map(image -> JournalResponseDTO.ImageUrl.builder()
                        .imageId(image.getId())
                        .url(gcsService.generateDownloadSignedUrl(image.getImageUrl()))
                        .build()).toList();

        return JournalConverter.toJournalDetail(journal, journalImageList);
    }

    public JournalResponseDTO.JournalListByMonth getCalendar(Long memberId, YearMonth month){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        LocalDate start = month.atDay(1);
        LocalDate end = month.plusMonths(1).atDay(1);
        List<Journal> journalList =
                journalRepository.findByMemberAndJournalDateGreaterThanEqualAndJournalDateLessThanOrderByJournalDateDesc(member, start, end);

        return JournalConverter.toJournalList(journalList, month);
    }

    // 날짜별 일기 조회
    public JournalResponseDTO.JournalByDate getJournalByDate(Long memberId, LocalDate date){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Couple couple = coupleRepository.getCoupleByMemberId(memberId)
                .orElseThrow(() -> new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER));

        Journal myJournal = journalRepository.findByMemberIdAndJournalDate(memberId, date)
                .orElse(null);
        List<JournalResponseDTO.ImageUrl> myJournalImageList = myJournal.getJournalImageList().stream()
                .map(image -> JournalResponseDTO.ImageUrl.builder()
                        .imageId(image.getId())
                        .url(gcsService.generateDownloadSignedUrl(image.getImageUrl()))
                        .build()).toList();
        Journal partnerJournal = journalRepository.findVisibleDiary(couple.getPartnerOf(memberId).getId(), date)
                .orElse(null);
        List<JournalResponseDTO.ImageUrl> partnerJournalImageList = partnerJournal.getJournalImageList().stream()
                .map(image -> JournalResponseDTO.ImageUrl.builder()
                        .imageId(image.getId())
                        .url(gcsService.generateDownloadSignedUrl(image.getImageUrl()))
                        .build()).toList();
        return JournalConverter.toJournalByDate(date, myJournal, myJournalImageList, partnerJournal, partnerJournalImageList);
    }

    public JournalResponseDTO.JournalListByMonth getJournalByMonth(Long memberId, YearMonth month){
        // TODO: 내 일기/상대방 일기 분기
        return null;
    }
}
