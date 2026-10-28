package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.AlarmSetting;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import gdg.challenge.poom.domain.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AlarmConverter {

    //AlarmResponseDTO.AlarmSetting
    public static AlarmResponseDTO.AlarmSetting toAlarmSetting(AlarmSetting alarmSetting, String deviceToken){
        return AlarmResponseDTO.AlarmSetting.builder()
                .allEnabled(alarmSetting.isAllEnabled())
                .chatEnabled(alarmSetting.isChatEnabled())
                .chatAlarmTime(alarmSetting.getChatAlarmTime())
                .sharedJournalEnabled(alarmSetting.isSharedJournalEnabled())
                .journalReminderEnabled(alarmSetting.isJournalReminderEnabled())
                .journalReminderTime(alarmSetting.getJournalReminderTime())
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
                .title(alarm.getDescription())
                .alarmType(alarm.getAlarmType())
                .description(alarm.getDescription())
                .characterType(alarm.getCharacterType())
                .alarmListVerContent(alarm.getAlarmListVerContent())
                .timeAgo(timeAgo)
                .journalId(alarm.getJournalId())
                .journalDate(alarm.getJournalDate())
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
                .journalId(request.journalId())
                .journalDate(request.journalDate())
                .member(member)
                .build();
    }

    //
    public static AlarmRequestDTO.SendAlarm toSendAlarm(
            AlarmType alarmType, String description, String title,
            CharacterType characterType, String alarmListVerContent,LocalDateTime createdAt
    ){
        return toSendAlarm(alarmType, description, title, characterType, alarmListVerContent, createdAt, null, null);
    }

    public static AlarmRequestDTO.SendAlarm toSendAlarm(
            AlarmType alarmType, String description, String title,
            CharacterType characterType, String alarmListVerContent, LocalDateTime createdAt,
            Long journalId, LocalDate journalDate
    ){
        return AlarmRequestDTO.SendAlarm.builder()
                .title(title)
                .alarmType(alarmType)
                .description(description)
                .characterType(characterType)
                .alarmListVerContent(alarmListVerContent)
                .createdAt(createdAt)
                .journalId(journalId)
                .journalDate(journalDate)
                .build();
    }

}
