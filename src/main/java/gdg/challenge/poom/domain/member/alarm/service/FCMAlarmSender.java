package gdg.challenge.poom.domain.member.alarm.service;

import com.google.firebase.messaging.Message;
import gdg.challenge.poom.domain.member.alarm.generator.FCMAlarmMessageGenerator;
import gdg.challenge.poom.domain.member.alarm.sender.FCMAlarmSendUtil;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMAlarmSender {
    private final MemberRepository memberRepository;
    private final FCMAlarmSendUtil fcmAlarmSendUtil;
    private final FCMAlarmMessageGenerator fcmAlarmMessageGenerator;

    public void send(Member member, AlarmRequestDTO.SendAlarm request) throws Exception{
        try {
            // 메시지 생성
            Message message = fcmAlarmMessageGenerator.generate(member, request);
            // 메시지 전송
            fcmAlarmSendUtil.send(message);
        } catch (Exception e) {
            log.warn("Alarm error", e);
            throw e;
        }
    }

}
