package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByMemberAndJournalDateGreaterThanEqualAndJournalDateLessThanOrderByJournalDateDesc(
            Member member,
            LocalDate start,
            LocalDate end
    );

    Optional<Journal> findByMemberIdAndJournalDate(Long memberId, LocalDate date);

    @Query("""
    select j from Journal j
        where j.member.id = :memberId
            and j.journalDate = :date
            and j.visibility in :visibilities
    """)
    Optional<Journal> findVisibleDiary(Long memberId, LocalDate date);
}
