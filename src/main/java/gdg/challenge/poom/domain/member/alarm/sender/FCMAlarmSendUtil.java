package gdg.challenge.poom.domain.member.alarm.sender;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import gdg.challenge.poom.global.error.code.status.AlarmErrorCode;
import gdg.challenge.poom.global.error.exception.handler.AlarmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FCMAlarmSendUtil {

    public String send(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공. response={}", response);
            return response;

        } catch (FirebaseMessagingException e) {
            log.error("FCM 전송 실패. errorCode={}, message={}",
                    e.getMessagingErrorCode(), e.getMessage(), e);
            MessagingErrorCode errorCode = e.getMessagingErrorCode();

            if (errorCode == MessagingErrorCode.UNREGISTERED) {
                throw new AlarmException(AlarmErrorCode.FCM_TOKEN_EXPIRED);
            } else if (errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                throw new AlarmException(AlarmErrorCode.FCM_INVALID_TOKEN);
            } else {
                throw new AlarmException(AlarmErrorCode.FCM_SEND_FAIL);
            }
        }
    }
}
