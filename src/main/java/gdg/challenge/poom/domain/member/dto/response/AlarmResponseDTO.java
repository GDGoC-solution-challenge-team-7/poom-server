package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import lombok.Builder;

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
            String timeAgo
    ){}

    @Builder
    public record AlarmSetting(
        Boolean pushAlarm,
        LocalTime dailyAlarmTime,
        String deviceToken
    ){}
}
