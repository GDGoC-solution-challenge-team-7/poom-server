package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByMemberA_IdOrMemberB_Id(Long memberAId, Long memberBId);
}
