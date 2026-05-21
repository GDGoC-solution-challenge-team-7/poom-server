package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalImageRepository extends JpaRepository<JournalImage, Long> {
    List<JournalImage> findByJournal(Journal journal);
    void deleteByJournal(Journal journal);
}
