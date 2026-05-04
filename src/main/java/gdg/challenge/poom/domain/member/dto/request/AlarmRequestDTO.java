package gdg.challenge.poom.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AlarmRequestDTO (){

    @Builder
    public record SendAlarm(
            @NotBlank
            String title,
            @NotNull
            AlarmType alarmType,
            @NotBlank
            String description,
            @NotNull
            CharacterType characterType,
            @NotBlank
            String alarmListVerContent,
            @NotNull
            LocalDateTime createdAt
    ) {

    }

    public record UpdateDeviceToken (
            @NotBlank
            String deviceToken
    ){
    }

    public record UpdateAlarmSetting(
            @NotNull
            Boolean pushAlarm,
            @Schema(
                    type = "string",
                    example = "12:34:00",
                    description = "매일 알림 시간"
            )
            @JsonFormat(pattern = "HH:mm:ss") @NotNull
            LocalTime dailyAlarmTime
    ){}
}
