package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByMemberAndJournalDateGreaterThanEqualAndJournalDateLessThan(
            Member member,
            LocalDate start,
            LocalDate end
    );
}
