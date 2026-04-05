package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.alarm.service.FCMAlarmSender;
import gdg.challenge.poom.domain.member.converter.AlarmConverter;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.enums.AlarmType;
import gdg.challenge.poom.domain.member.repository.AlarmRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AlarmCommandService {

    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final FCMAlarmSender fcmAlarmSender;
    private static final int BATCH_SIZE = 500;

    public void send(Long memberId, AlarmRequestDTO.SendAlarm request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        fcmAlarmSender.send(member, request);
        alarmRepository.save(AlarmConverter.toAlarm(member, request));
    }

    public void updateDeviceToken(AlarmRequestDTO.UpdateDeviceToken request, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateDeviceToken(request.deviceToken());
    }

    public void updateAlarmSettings(AlarmRequestDTO.UpdateAlarmSetting request, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateAlarmSetting(request.pushAlarm(), request.dailyAlarmTime());
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
            String alarmText = target.getCharacterType() + " is here. Shall we talk?";
            AlarmRequestDTO.SendAlarm sendAlarm = AlarmConverter.toSendAlarm(
                    AlarmType.CHAT, alarmText, null, target.getCharacterType(), null
            );

            // 1. 알림 발송
            fcmAlarmSender.send(target, sendAlarm);
            // 2. 다음 발송 시간 갱신 (다음날)
            target.updateDailyAlarmDateLogic(target.getNextSendAt().plusDays(1), LocalDateTime.now());
        } catch (Exception e) {}
    }

}
