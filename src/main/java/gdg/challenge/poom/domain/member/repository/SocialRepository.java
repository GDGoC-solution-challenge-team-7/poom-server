package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRepository extends JpaRepository<Social, Long> {
    Optional<Social> findByProviderIdAndSocialType(String providerId, SocialType socialType);
}
