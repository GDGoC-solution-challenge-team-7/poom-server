package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JournalImageRepository extends JpaRepository<JournalImage, Long> {
    List<JournalImage> findByJournal(Journal journal);
    void deleteByJournal(Journal journal);
    @Query("""
    select ji.imageUrl
    from JournalImage ji
    where ji.journal.member.id = :memberId
    """)
    List<String> findImageUrlsByMemberId(Long memberId);
}
