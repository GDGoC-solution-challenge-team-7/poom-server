package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
        SELECT m FROM Member m
        WHERE m.pushAlarm = true
          AND m.nextSendAt <= :now
        ORDER BY m.nextSendAt ASC
        """)
    List<Member> findDueTargets(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
