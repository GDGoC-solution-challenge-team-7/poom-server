package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCoupleCode(String coupleCode);

//    @Query("""
//        SELECT m FROM Member m
//        WHERE m.pushAlarm = true
//          AND m.nextSendAt <= :now
//        ORDER BY m.nextSendAt ASC
//        """)
//    List<Member> findDueTargets(
//            @Param("now") LocalDateTime now,
//            Pageable pageable
//    );

    Optional<Member> findByCoupleCode(String coupleCode);

    // 삭제 여부 상관없이 조회 (@SQLRestriction 우회)
    @Query(value = "SELECT * FROM member WHERE member_id = :memberId", nativeQuery = true)
    Optional<Member> findIncludingDeletedById(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE member SET status = 'ACTIVE', deleted_at = NULL WHERE member_id = :id",
            nativeQuery = true)
    int restoreById(@Param("id") Long id);

    // 유예 기간이 지난 soft delete 사용자 ID 조회 (@SQLRestriction 우회를 위해 native)
    @Query(value = """
            SELECT id FROM member
            WHERE deleted_at IS NOT NULL
              AND deleted_at < :threshold
            """, nativeQuery = true)
    List<Long> findExpiredDeletedUserIds(@Param("threshold") LocalDateTime threshold);

    // 삭제 여부와 상관없이 ID 목록으로 hard delete
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM users WHERE id IN (:ids)", nativeQuery = true)
    int hardDeleteByIdIn(@Param("ids") Collection<Long> ids);
}
