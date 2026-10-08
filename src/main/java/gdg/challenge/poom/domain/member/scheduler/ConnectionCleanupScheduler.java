package gdg.challenge.poom.domain.member.scheduler;

import gdg.challenge.poom.domain.member.service.CoupleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectionCleanupScheduler {
    private final CoupleCommandService coupleCommandService;

    // 매일 새벽 3시 만료된 부부 연결 데이터 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        coupleCommandService.deleteExpiredConnections();
    }

}
