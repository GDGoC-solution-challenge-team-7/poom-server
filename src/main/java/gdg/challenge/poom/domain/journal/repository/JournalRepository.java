package gdg.challenge.poom.domain.journal.repository;

import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
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
    Optional<Journal> findVisibleDiary(
            @Param("memberId") Long memberId,
            @Param("date") LocalDate date,
            @Param("visibilities") List<Visibility> visibilities
    );

    // 영구 해제: 전부 나만 보기 + 백업 제거
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Journal j
           set j.visibility = :priv,
               j.previousVisibility = null
         where j.member.id in :memberIds
        """)
    int makeAllPrivateAndClearBackup(@Param("memberIds") Collection<Long> memberIds,
                                     @Param("priv") Visibility priv);

    /**
     * 유예 해제 시작: 나만 보기가 아닌 일기만 백업 후 나만 보기로 변경.
     * (주의) MySQL은 SET 절을 왼쪽부터 순서대로 평가하므로
     * previousVisibility 대입이 visibility 대입보다 먼저 와야 한다.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Journal j
           set j.previousVisibility = j.visibility,
               j.visibility = :priv
         where j.member.id in :memberIds
           and j.visibility <> :priv
           and j.previousVisibility is null
        """)
    int backupAndMakePrivate(@Param("memberIds") Collection<Long> memberIds, Visibility priv);

    /**
     * 유예 해제 후 복구: 백업된 값이 있는 일기만 원래대로.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Journal j
           set j.visibility = j.previousVisibility,
               j.previousVisibility = null
         where j.member.id in :memberIds
           and j.previousVisibility is not null
        """)
    int restoreVisibility(@Param("memberIds") Collection<Long> memberIds);

    boolean existsByJournalDate(LocalDate journalDate);
}
