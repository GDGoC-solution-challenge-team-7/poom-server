package gdg.challenge.poom.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AlarmRequestDTO (){

    @Builder
    public record SendAlarm(
            AlarmType alarmType,
            String description,
            String imageUrl,
            CharacterType characterType,
            LocalDateTime createdAt
    ) {

    }

    public record UpdateDeviceToken (
            String deviceToken
    ){
    }

    public record UpdateAlarmSetting(
            Boolean pushAlarm,
            @JsonFormat(pattern = "HH:mm:ss")
            LocalTime dailyAlarmTime
    ){}
}
