package gdg.challenge.poom.domain.member.scheduler;

import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.repository.SocialRepository;
import gdg.challenge.poom.domain.member.service.CoupleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectionCleanupScheduler {
    private final CoupleCommandService coupleCommandService;
    private final SocialRepository socialRepository;
    private final MemberRepository memberRepository;

    // 매일 새벽 3시 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        // 만료된 부부 연결 데이터 삭제
        coupleCommandService.deleteExpiredConnections();
        // 만료된 사용자 계정 삭제
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<Long> ids = memberRepository.findExpiredDeletedUserIds(threshold);  // native: deleted_at < :threshold
        if (ids.isEmpty()) return;

        socialRepository.deleteAllByMemberIdIn(ids);
        memberRepository.deleteAllByIdInBatch(ids);
    }

}
