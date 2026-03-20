package gdg.challenge.poom.domain.member.alarm.sender;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class FCMAlarmSendUtil {
    public void send(Message message) throws Exception{
        FirebaseMessaging.getInstance().send(message);
    }
}
