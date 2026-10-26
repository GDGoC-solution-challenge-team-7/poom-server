package gdg.challenge.poom.domain.member.alarm.generator;


import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FCMAlarmMessageGenerator {

    // 메시지 생성
    public Message generate(Member member, AlarmRequestDTO.SendAlarm request){
        return Message.builder()
                .setNotification(toNotification(request))
                .putAllData(toData(request))
                .setToken(member.getDeviceToken())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH) // 우선순위
                        .setNotification(AndroidNotification.builder()
                                .setChannelId("high_importance_channel") // 프론트와 일치시켜야 함
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }

    // 클릭 시 상세 화면 이동을 위한 식별 정보
    public Map<String, String> toData(AlarmRequestDTO.SendAlarm request){
        Map<String, String> data = new HashMap<>();
        data.put("alarmType", request.alarmType().name());
        if (request.journalId() != null) {
            data.put("journalId", String.valueOf(request.journalId()));
        }
        if (request.journalDate() != null) {
            data.put("journalDate", request.journalDate().toString());
        }
        return data;
    }

    // 메시지에 넣을 Notification 생성
    public Notification toNotification(AlarmRequestDTO.SendAlarm request){
        return Notification.builder()
                .setTitle(request.title())
                .setBody(request.description())
                .build();
    }
}
