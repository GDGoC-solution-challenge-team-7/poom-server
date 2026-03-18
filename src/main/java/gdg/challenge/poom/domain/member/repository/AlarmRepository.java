package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByMember(Member member);
}
