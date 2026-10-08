package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Couple;
import gdg.challenge.poom.domain.member.entity.enums.CoupleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByMemberA_IdOrMemberB_Id(Long memberAId, Long memberBId);

    @Query("""
        select count(c) > 0
        from Couple c
        where (c.memberA.id = :memberId or c.memberB.id = :memberId)
          and c.coupleStatus in :statuses
        """)
    boolean existsByMemberIdAndStatus(@Param("memberId") Long memberId,
                                      @Param("status") Collection<CoupleStatus> statuses);

    // 실제 삭제 예정 시각이 지난 부부들 조회
    List<Couple> findByCoupleStatusAndDeleteScheduledAtBefore(
            CoupleStatus status, LocalDateTime now);
}
