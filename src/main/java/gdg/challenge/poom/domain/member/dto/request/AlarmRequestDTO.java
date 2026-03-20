package gdg.challenge.poom.domain.member.dto.request;

import gdg.challenge.poom.domain.member.entity.enums.AlarmType;

import java.time.LocalTime;

public record AlarmRequestDTO (){

    public record SendAlarm(
            String title,
            String description,
            AlarmType alarmType,
            String imageUrl
    ) {

    }

    public record UpdateDeviceToken (
            String deviceToken
    ){
    }

    public record UpdateAlarmSetting(
            Boolean pushAlarm,
            LocalTime dailyAlarmTime
    ){}
}
