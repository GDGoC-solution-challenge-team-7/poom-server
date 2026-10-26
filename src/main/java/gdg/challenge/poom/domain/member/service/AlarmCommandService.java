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
import gdg.challenge.poom.global.error.code.status.AlarmErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.AlarmException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        alarmSetting.updateAlarmSetting(request, LocalDateTime.now());
        return AlarmConverter.toAlarmSetting(alarmSetting, member.getDeviceToken());
    }

    public AlarmResponseDTO.UpdateAllAlarm updateAllAlarmSettings(AlarmRequestDTO.UpdateAllAlarm request, Long memberId){
        AlarmSetting alarmSetting = alarmSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_SETTING_NOT_FOUND));

        alarmSetting.updateAll(request.allEnabled());
        return AlarmResponseDTO.UpdateAllAlarm.builder().allEnabled(alarmSetting.isAllEnabled()).build();
    }

    public AlarmSetting createDefault(Member member) {
        AlarmSetting setting = AlarmSetting.builder()
                .member(member)
                .build();                              // 기본값(9시, enabled=true) 적용

        setting.initSchedule(LocalDateTime.now());     // 첫 발송 시각 계산
        return alarmSettingRepository.save(setting);
    }

    public void dispatchDueAlarms(){
        LocalDateTime now = LocalDateTime.now();
        dispatchChat(now);
        dispatchJournal(now);
    }

    private void dispatchChat(LocalDateTime now) {
        List<AlarmSetting> alarmSettingList =
                alarmSettingRepository.findDueChat(now, PageRequest.of(0, BATCH_SIZE));

        for(AlarmSetting alarmSetting : alarmSettingList){
            try {
                Member member = alarmSetting.getMember();
                String alarmTitle = member.getCharacterType().getKoName() + "와 오늘도 대화해 볼까요?";
                String alarmContent = "오늘의 기분은 어떠신가요?\n" + member.getCharacterType().getKoName() + "와 함께 이야기해 볼까요?☺\uFE0F";
                String alarmListVerContent = member.getCharacterType() + " is here. Shall we talk? ";
                AlarmRequestDTO.SendAlarm sendAlarm = AlarmConverter.toSendAlarm(
                        AlarmType.CHAT, alarmContent, alarmTitle, member.getCharacterType(), alarmListVerContent,  LocalDateTime.now()
                );

                // 1. 알림 발송
                send(member.getId(), sendAlarm);
            } catch (Exception e) {
                log.error("알람 전송 실패 alarmSettingId={}", alarmSetting.getId(), e);
                // throw 하지 않고 로그만: 한 명 실패가 다른 사람에게 영향을 주지 않게
//            throw new AlarmException(AlarmErrorCode.FCM_SEND_FAIL);
            } finally {
                // 성공/실패 모두 다음 시각으로 넘겨서 한 건이 계속 앞자리를 막지 않게 함
                alarmSetting.markChatSent(now);
            }
        }
    }

    private void dispatchJournal(LocalDateTime now) {
        List<AlarmSetting> alarmSettingList =
                alarmSettingRepository.findDueChat(now, PageRequest.of(0, BATCH_SIZE));

        for(AlarmSetting alarmSetting : alarmSettingList){
            try {
                Member member = alarmSetting.getMember();
                String alarmTitle = "오늘 하루는 어땠나요? 일기를 남겨보세요 📝";
                String alarmContent = "일기 쓸 시간이에요";
                String alarmListVerContent = "오늘 하루는 어땠나요? 일기를 남겨보세요 📝";
                AlarmRequestDTO.SendAlarm sendAlarm = AlarmConverter.toSendAlarm(
                        AlarmType.JOURNAL, alarmContent, alarmTitle, member.getCharacterType(), alarmListVerContent,  LocalDateTime.now()
                );

                // 1. 알림 발송
                send(member.getId(), sendAlarm);
            } catch (Exception e) {
                log.error("알람 전송 실패 alarmSettingId={}", alarmSetting.getId(), e);
                // throw 하지 않고 로그만: 한 명 실패가 다른 사람에게 영향을 주지 않게
//            throw new AlarmException(AlarmErrorCode.FCM_SEND_FAIL);
            } finally {
                // 성공/실패 모두 다음 시각으로 넘겨서 한 건이 계속 앞자리를 막지 않게 함
                alarmSetting.markJournalSent(now);
            }
        }
    }

}
