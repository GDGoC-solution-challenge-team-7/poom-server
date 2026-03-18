package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;

import java.time.LocalTime;
import java.util.List;

public class AlarmConverter {

    //AlarmResponseDTO.AlarmSetting
    public static AlarmResponseDTO.AlarmSetting toAlarmSetting(Boolean pushAlarm, LocalTime dailyAlarmTime){
        return AlarmResponseDTO.AlarmSetting.builder()
                .pushAlarm(pushAlarm)
                .dailyAlarmTime(dailyAlarmTime)
                .build();
    }

    // AlarmResponseDTO.AlarmList
    public static AlarmResponseDTO.AlarmList toAlarmList(List<Alarm> alarms){
        List<AlarmResponseDTO.Alarm> alarmList = alarms.stream()
                .map(AlarmConverter::toAlarm)
                .toList();

        return AlarmResponseDTO.AlarmList.builder()
                .alarms(alarmList)
                .build();
    }

    public static AlarmResponseDTO.Alarm toAlarm(Alarm alarm){
        return AlarmResponseDTO.Alarm.builder()
                .title(alarm.getTitle())
                .alarmType(alarm.getAlarmType())
                .description(alarm.getDescription())
                // TODO: ImageUrl도 같이 보내기 GCS 구현 후
                .imageUrl(null)
                .build();
    }

}
