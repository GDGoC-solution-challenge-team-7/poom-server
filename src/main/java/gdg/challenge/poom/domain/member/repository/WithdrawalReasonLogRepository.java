package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.auth.entity.WithdrawalReasonLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalReasonLogRepository extends JpaRepository<WithdrawalReasonLog, Long> {
}
