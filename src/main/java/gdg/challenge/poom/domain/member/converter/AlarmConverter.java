package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.Member;

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
                .map(AlarmConverter::toAlarmItem)
                .toList();

        return AlarmResponseDTO.AlarmList.builder()
                .alarms(alarmList)
                .build();
    }

    public static AlarmResponseDTO.Alarm toAlarmItem(Alarm alarm){
        return AlarmResponseDTO.Alarm.builder()
                .title(alarm.getTitle())
                .alarmType(alarm.getAlarmType())
                .description(alarm.getDescription())
                // TODO: ImageUrl도 같이 보내기 GCS 구현 후
                .imageUrl(null)
                .build();
    }

    // 저장하기 위한 Alarm 만들기
    public static Alarm toAlarm(Member member, AlarmRequestDTO.SendAlarm request){
        return Alarm.builder()
                .title(request.title())
                .description(request.description())
                .alarmType(request.alarmType())
                .member(member)
                .build();
    }

}
