package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface SocialRepository extends JpaRepository<Social, Long> {
    Optional<Social> findByProviderIdAndSocialType(String providerId, SocialType socialType);

    // 단일 사용자의 Social 삭제 (탈퇴 시 사용)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Social s WHERE s.member.id = :memberId")
    void deleteAllByMemberId(Long memberId);

    // 여러 사용자의 Social 삭제 (스케줄러에서 사용)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Social s WHERE s.member.id IN :memberIds")
    int deleteAllByMemberIdIn(@Param("memberIds") Collection<Long> memberIds);
}
