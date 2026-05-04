package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import gdg.challenge.poom.domain.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AlarmConverter {

    //AlarmResponseDTO.AlarmSetting
    public static AlarmResponseDTO.AlarmSetting toAlarmSetting(Boolean pushAlarm, LocalTime dailyAlarmTime, String deviceToken){
        return AlarmResponseDTO.AlarmSetting.builder()
                .pushAlarm(pushAlarm)
                .dailyAlarmTime(dailyAlarmTime)
                .deviceToken(deviceToken)
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
        String timeAgo = TimeUtil.toTimeAgo(alarm.getCreatedAt());

        return AlarmResponseDTO.Alarm.builder()
                .alarmType(alarm.getAlarmType())
                .description(alarm.getDescription())
                .characterType(alarm.getCharacterType())
                .timeAgo(timeAgo)
                .build();
    }

    // 저장하기 위한 Alarm 만들기
    public static Alarm toAlarm(Member member, AlarmRequestDTO.SendAlarm request){
        return Alarm.builder()
                .title(request.title())
                .description(request.description())
                .alarmType(request.alarmType())
                .characterType(request.characterType())
                .alarmListVerContent(request.alarmListVerContent())
                .member(member)
                .build();
    }

    //
    public static AlarmRequestDTO.SendAlarm toSendAlarm(
            AlarmType alarmType, String description, String title,
            CharacterType characterType, String alarmListVerContent,LocalDateTime createdAt
    ){
        return AlarmRequestDTO.SendAlarm.builder()
                .title(title)
                .alarmType(alarmType)
                .description(description)
                .characterType(characterType)
                .alarmListVerContent(alarmListVerContent)
                .createdAt(createdAt)
                .build();
    }

}
