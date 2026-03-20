package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.alarm.service.FCMAlarmSender;
import gdg.challenge.poom.domain.member.converter.AlarmConverter;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.AlarmRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AlarmCommandService {

    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final FCMAlarmSender fcmAlarmSender;

    public void send(Long memberId, AlarmRequestDTO.SendAlarm request) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            fcmAlarmSender.send(member, request);
            alarmRepository.save(AlarmConverter.toAlarm(member, request));
        } catch (Exception e) {}
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

}
