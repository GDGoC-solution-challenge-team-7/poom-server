package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AlarmResponseDTO() {

    @Builder
    public record AlarmList(
        List<AlarmResponseDTO.Alarm> alarms
    ){}

    @Builder
    public record Alarm(
            String title,
            AlarmType alarmType,
            String description,
            CharacterType characterType,
            String alarmListVerContent,
            String timeAgo,
            Long journalId,
            LocalDate journalDate
    ){}

    @Builder
    public record AlarmSetting(
        Boolean allEnabled,
        Boolean chatEnabled,
        LocalTime chatAlarmTime,
        Boolean sharedJournalEnabled,
        Boolean journalReminderEnabled,
        LocalTime journalReminderTime,
        String deviceToken
    ){}

    @Builder
    public record UpdateAllAlarm(
            Boolean allEnabled
    ){}
}
