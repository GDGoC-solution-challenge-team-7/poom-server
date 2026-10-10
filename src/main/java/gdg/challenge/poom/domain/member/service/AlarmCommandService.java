package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.alarm.service.FCMAlarmSender;
import gdg.challenge.poom.domain.member.converter.AlarmConverter;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.AlarmSetting;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import gdg.challenge.poom.domain.member.repository.AlarmRepository;
import gdg.challenge.poom.domain.member.repository.AlarmSettingRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.util.TimeUtil;
import gdg.challenge.poom.global.error.code.status.AlarmErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.AlarmException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AlarmCommandService {

    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmSettingRepository alarmSettingRepository;
    private final FCMAlarmSender fcmAlarmSender;
    private static final int BATCH_SIZE = 500;

    public void send(Long memberId, AlarmRequestDTO.SendAlarm request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        fcmAlarmSender.send(member, request);
        Alarm alarm = AlarmConverter.toAlarm(member, request);
        log.info("FCM 전송 성공 후, 저장되어야 함. description={}", alarm.getDescription());
        member.addAlarm(alarm);
        alarmRepository.save(alarm);
    }

    public void updateDeviceToken(AlarmRequestDTO.UpdateDeviceToken request, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateDeviceToken(request.deviceToken());
    }

    public AlarmResponseDTO.AlarmSetting updateAlarmSettings(AlarmRequestDTO.UpdateAlarmSetting request, Long memberId){
        AlarmSetting alarmSetting = alarmSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_SETTING_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        LocalDateTime nextSendAt = TimeUtil.calculateNextSendAt(request.chatAlarmTime());
        alarmSetting.updateNextSendAt(nextSendAt);
        alarmSetting.updateAlarmSetting(request);
        return AlarmConverter.toAlarmSetting(alarmSetting, member.getDeviceToken());
    }

    public AlarmResponseDTO.UpdateAllAlarm updateAllAlarmSettings(AlarmRequestDTO.UpdateAllAlarm request, Long memberId){
        AlarmSetting alarmSetting = alarmSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_SETTING_NOT_FOUND));

        alarmSetting.updateAll(request.allEnabled());
        return AlarmResponseDTO.UpdateAllAlarm.builder().allEnabled(alarmSetting.isAllEnabled()).build();
    }

    public void dispatchDueAlarms(){
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        List<Member> targetMembers = memberRepository.findDueTargets(now, pageable);
        if (targetMembers.isEmpty()) return;

        for (Member target : targetMembers) sendAndUpdate(target);
    }

    private void sendAndUpdate(Member target) {
        try {
            String alarmTitle = target.getCharacterType().getKoName() + "와 오늘도 대화해 볼까요?";
            String alarmContent = "오늘의 기분은 어떠신가요?\n" + target.getCharacterType().getKoName() + "와 함께 이야기해 볼까요?☺\uFE0F";
            String alarmListVerContent = target.getCharacterType() + " is here. Shall we talk? ";
            AlarmRequestDTO.SendAlarm sendAlarm = AlarmConverter.toSendAlarm(
                    AlarmType.CHAT, alarmContent, alarmTitle, target.getCharacterType(), alarmListVerContent,  LocalDateTime.now()
            );

            // 1. 알림 발송
            send(target.getId(), sendAlarm);
            // 2. 다음 발송 시간 갱신 (다음날)
            target.updateDailyAlarmDateLogic(LocalDate.now()
                    .plusDays(1)
                    .atTime(target.getDailyAlarmTime()), LocalDateTime.now());
        } catch (Exception e) {
            log.error("알람 전송 실패 memberId={}", target.getId(), e);
            throw new AlarmException(AlarmErrorCode.FCM_SEND_FAIL);
        }
    }

}
