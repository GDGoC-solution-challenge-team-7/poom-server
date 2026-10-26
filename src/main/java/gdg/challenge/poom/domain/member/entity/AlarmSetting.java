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
public class AlarmSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_setting_id")
    private Long id;

    @Builder.Default
    private boolean allEnabled = true;

    @Builder.Default
    private boolean chatEnabled = true;

    @Builder.Default
    private LocalTime chatAlarmTime= LocalTime.of(9, 0);

    private LocalDateTime chatNextSendAt;
    private LocalDateTime chatLastSendAt;

    @Builder.Default
    private boolean sharedJournalEnabled = true;

    @Builder.Default
    private boolean journalReminderEnabled = true;

    @Builder.Default
    private LocalTime journalReminderTime= LocalTime.of(9, 0);

    private LocalDateTime journalNextSendAt;
    private LocalDateTime journalLastSendAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

//    public void updateNextSendAt(LocalDateTime nextSendAt) {
//        this.nextSendAt = nextSendAt;
//    }
//
//    public void updateDailyAlarmDateLogic(LocalDateTime nextSendAt, LocalDateTime lastSendAt) {
//        this.nextSendAt = nextSendAt;
//        this.lastSendAt = lastSendAt;
//    }

    // 전체 토글: 전부 켜기/끄기
    public void updateAll(boolean allEnabled){
        this.allEnabled = allEnabled;
        this.chatEnabled = allEnabled;
        this.sharedJournalEnabled = allEnabled;
        this.journalReminderEnabled = allEnabled;
        if (allEnabled) {
            initSchedule(LocalDateTime.now());   // 켤 때만 다음 발송 시각 재계산
        }
    }

    public void updateAlarmSetting(AlarmRequestDTO.UpdateAlarmSetting request, LocalDateTime now) {
        boolean chatChanged = this.chatEnabled != request.chatEnabled()
                || !this.chatAlarmTime.equals(request.chatAlarmTime());
        this.chatEnabled = request.chatEnabled();
        this.chatAlarmTime = request.chatAlarmTime();
        if (chatChanged) {
            this.chatNextSendAt = firstFireAt(this.chatAlarmTime, now);
        }

        boolean journalChanged = this.journalReminderEnabled != request.journalReminderEnabled()
                || !this.journalReminderTime.equals(request.journalReminderTime());
        this.sharedJournalEnabled = request.sharedJournalEnabled();
        this.journalReminderEnabled = request.journalReminderEnabled();
        if (journalChanged) {
            this.journalNextSendAt = firstFireAt(this.journalReminderTime, now);
        }


        this.journalReminderTime = request.journalReminderTime();
        syncAllEnabled();
    }

    // 개별 설정 변경 시 allEnabled 재계산 (발송 여부 판단에는 사용되지 않는 표시용 값)
    private void syncAllEnabled() {
        this.allEnabled = this.chatEnabled
                && this.sharedJournalEnabled
                && this.journalReminderEnabled;
    }

    // ---------- 스케줄 계산 ----------

    /** 생성 직후 호출: 두 알림의 첫 발송 시각 설정 */
    public void initSchedule(LocalDateTime now) {
        this.chatNextSendAt = firstFireAt(chatAlarmTime, now);
        this.journalNextSendAt = firstFireAt(journalReminderTime, now);
    }

    /** 채팅 알림 시간/활성화 변경 시 */
    public void changeChatAlarm(boolean enabled, LocalTime time, LocalDateTime now) {
        this.chatEnabled = enabled;
        this.chatAlarmTime = time;
        this.chatNextSendAt = firstFireAt(time, now);
    }

    /** 일기 리마인드 시간/활성화 변경 시 */
    public void changeJournalReminder(boolean enabled, LocalTime time, LocalDateTime now) {
        this.journalReminderEnabled = enabled;
        this.journalReminderTime = time;
        this.journalNextSendAt = firstFireAt(time, now);
    }

    /** 채팅 알림 발송 처리 후 */
    public void markChatSent(LocalDateTime now) {
        this.chatLastSendAt = now;
        this.chatNextSendAt = nextAfter(chatNextSendAt, now);
    }

    /** 일기 리마인드 발송 처리 후 */
    public void markJournalSent(LocalDateTime now) {
        this.journalLastSendAt = now;
        this.journalNextSendAt = nextAfter(journalNextSendAt, now);
    }

    // ---------- 내부 계산 ----------

    /** 오늘 해당 시각이 아직 안 지났으면 오늘, 지났으면 내일 */
    private LocalDateTime firstFireAt(LocalTime time, LocalDateTime now) {
        LocalDateTime next = now.toLocalDate().atTime(time);
        return next.isAfter(now) ? next : next.plusDays(1);
    }

    /** 기존 예정 시각 기준 +1일, 지연돼도 미래가 될 때까지 보정 */
    private LocalDateTime nextAfter(LocalDateTime current, LocalDateTime now) {
        LocalDateTime next = current.plusDays(1);
        while (!next.isAfter(now)) next = next.plusDays(1);
        return next;
    }

}
