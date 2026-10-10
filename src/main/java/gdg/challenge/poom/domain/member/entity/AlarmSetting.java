package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "alarm_setting")
public class AlarmSetting  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_setting_id")
    private Long id;

    private boolean allEnabled;

    private boolean chatEnabled;

    @Builder.Default
    private LocalTime chatAlarmTime= LocalTime.of(9, 0);

    private boolean sharedJournalEnabled;

    private boolean journalReminderEnabled;

    @Builder.Default
    private LocalTime journalReminderTime= LocalTime.of(9, 0);

    private LocalDateTime nextSendAt;

    @Builder.Default
    private LocalDateTime lastSendAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    public void updateNextSendAt(LocalDateTime nextSendAt) {
        this.nextSendAt = nextSendAt;
    }

    // 전체 토글: 전부 켜기/끄기
    public void updateAll(boolean allEnabled){
        this.allEnabled = allEnabled;
        this.chatEnabled = allEnabled;
        this.sharedJournalEnabled = allEnabled;
        this.journalReminderEnabled = allEnabled;
    }

    public void updateAlarmSetting(AlarmRequestDTO.UpdateAlarmSetting request) {
        this.chatEnabled = request.chatEnabled();
        this.chatAlarmTime = request.chatAlarmTime();
        this.sharedJournalEnabled = request.sharedJournalEnabled();
        this.journalReminderEnabled = request.journalReminderEnabled();
        this.journalReminderTime = request.journalReminderTime();
        syncAllEnabled();
    }

    // 개별 설정 변경 시 allEnabled 재계산
    private void syncAllEnabled() {
        this.allEnabled = this.chatEnabled
                && this.sharedJournalEnabled
                && this.journalReminderEnabled;
    }
}
