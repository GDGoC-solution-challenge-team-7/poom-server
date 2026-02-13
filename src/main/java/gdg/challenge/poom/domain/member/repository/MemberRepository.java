package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
