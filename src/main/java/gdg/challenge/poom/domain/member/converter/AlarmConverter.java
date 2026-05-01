package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import gdg.challenge.poom.domain.util.TimeAgoUtil;

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
    public static AlarmResponseDTO.AlarmList toAlarmList(List<Alarm> alarms, CharacterType characterType){
        List<AlarmResponseDTO.Alarm> alarmList = alarms.stream()
                .map(alarm ->AlarmConverter.toAlarmItem(alarm, characterType))
                .toList();

        return AlarmResponseDTO.AlarmList.builder()
                .alarms(alarmList)
                .build();
    }

    public static AlarmResponseDTO.Alarm toAlarmItem(Alarm alarm, CharacterType characterType){
        String timeAgo = TimeAgoUtil.toTimeAgo(alarm.getCreatedAt());

        return AlarmResponseDTO.Alarm.builder()
                .alarmType(alarm.getAlarmType())
                .description(alarm.getDescription())
                .characterType(characterType)
                .timeAgo(timeAgo)
                // TODO: ImageUrl도 같이 보내기 GCS 구현 후
                .imageUrl(null)
                .build();
    }

    // 저장하기 위한 Alarm 만들기
    public static Alarm toAlarm(Member member, AlarmRequestDTO.SendAlarm request){
        return Alarm.builder()
                .description(request.description())
                .alarmType(request.alarmType())
                .member(member)
                .build();
    }

    //
    public static AlarmRequestDTO.SendAlarm toSendAlarm(
            AlarmType alarmType, String description, String imageUrl,
            CharacterType characterType, LocalDateTime createdAt
    ){
        return AlarmRequestDTO.SendAlarm.builder()
                .alarmType(alarmType)
                .description(description)
                .imageUrl(imageUrl)
                .characterType(characterType)
                .createdAt(createdAt)
                .build();
    }

}
