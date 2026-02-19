package gdg.challenge.poom.domain.voice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

/**
 * 시연용 WebSocket 연결 정보.
 * REST API로 반환되며, 클라이언트는 webSocketUrl로 음성 비서 서버에 연결합니다.
 * available == false 이면 해당 서버에 연결할 수 없으므로 클라이언트에서 안내 메시지를 표시하면 됩니다.
 */
@Getter
@Builder
public class VoiceConnectionInfo {

    /** 음성 비서 기능 사용 가능 여부 (설정) */
    private final boolean enabled;

    /** 실시간 음성용 WebSocket 서버 URL (Pipecat 등) */
    @JsonProperty("webSocketUrl")
    private final String webSocketUrl;

    /** 클라이언트용 안내 문구 */
    private final String description;

    /** 음성 서버가 현재 열려 있어 연결 가능한지 여부 (TCP 연결 체크) */
    private final boolean available;

    /** available == false 일 때 클라이언트에 표시할 메시지 (선택) */
    private final String unavailableMessage;
}
