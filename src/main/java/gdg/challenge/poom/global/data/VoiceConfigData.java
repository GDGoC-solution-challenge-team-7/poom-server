package gdg.challenge.poom.global.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "voice")
public class VoiceConfigData {

    /** 음성 비서 기능 사용 여부 */
    private boolean enabled = true;

    /** 실시간 음성용 WebSocket 서버 URL (Pipecat 등) */
    private String websocketUrl = "ws://localhost:8765/ws";

    /** 클라이언트용 안내 문구 */
    private String description = "Gemini Live 기반 실시간 음성 비서 (STS). 모바일 앱에서 이 URL로 WebSocket 연결.";
}
