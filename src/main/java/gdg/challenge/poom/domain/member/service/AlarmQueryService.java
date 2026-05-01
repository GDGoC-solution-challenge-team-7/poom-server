package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.converter.AlarmConverter;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.entity.Alarm;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.AlarmRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlarmQueryService {

    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;

    public AlarmResponseDTO.AlarmList getAlarmList(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<Alarm> alarmList = alarmRepository.findByMember(member);
        return AlarmConverter.toAlarmList(alarmList, member.getCharacterType());
    }

    public AlarmResponseDTO.AlarmSetting getAlarmSettings(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return  AlarmConverter.toAlarmSetting(member.getPushAlarm(), member.getDailyAlarmTime(), member.getDeviceToken());
    }
}
