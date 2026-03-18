package gdg.challenge.poom.domain.member.dto.request;

import java.time.LocalTime;

public record AlarmRequestDTO (){

    public record UpdateDeviceToken (
            String deviceToken
    ){
    }

    public record UpdateAlarmSetting(
            Boolean pushAlarm,
            LocalTime dailyAlarmTime
    ){}
}
