package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByMemberAndJournalDateGreaterThanEqualAndJournalDateLessThanOrderByJournalDateDesc(
            Member member,
            LocalDate start,
            LocalDate end
    );

    @Query("""
        select distinct j
        from Journal j
        join fetch j.member
        left join fetch j.journalImageList
        where j.member.id = :memberId
          and j.journalDate between :start and :end
          and j.visibility in :visibilities
        order by j.journalDate desc
        """)
    List<Journal> findMonthly(
            @Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("visibilities") List<Visibility> visibilities
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
