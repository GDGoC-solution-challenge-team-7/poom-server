package gdg.challenge.poom.domain.voice.service;

import gdg.challenge.poom.global.data.VoiceConfigData;
import gdg.challenge.poom.domain.voice.dto.VoiceConnectionInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 음성 비서 연결 정보를 제공합니다.
 * 실제 WebSocket 음성 세션은 별도 서버(Pipecat/FastAPI + Gemini Live)에서 운영하며,
 * 시연 시 클라이언트가 연결할 WebSocket URL 등을 REST로 반환합니다.
 * 해당 서버가 열려 있는지는 TCP 연결로 확인하여 available 필드로 반환합니다.
 */
@Service
@RequiredArgsConstructor
public class VoiceService {

    private static final Logger log = LoggerFactory.getLogger(VoiceService.class);
    private static final int CONNECT_TIMEOUT_MS = 2_000;

    private final VoiceConfigData voiceConfigData;

    /**
     * 시연용 WebSocket 연결 정보를 반환합니다.
     * 음성 서버(webSocketUrl)에 실제로 연결 가능한지 TCP로 확인하고, available 필드에 반영합니다.
     */
    public VoiceConnectionInfo getConnectionInfo() {
        String url = voiceConfigData.getWebsocketUrl();
        boolean available = voiceConfigData.isEnabled() && isVoiceServerReachable(url);
        String unavailableMessage = available ? null : "음성 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해 주세요.";

        return VoiceConnectionInfo.builder()
                .enabled(voiceConfigData.isEnabled())
                .webSocketUrl(url)
                .description(voiceConfigData.getDescription())
                .available(available)
                .unavailableMessage(unavailableMessage)
                .build();
    }

    /**
     * WebSocket URL의 호스트:포트에 TCP 연결이 되는지 확인합니다.
     * (실제 WebSocket 핸드셰이크는 하지 않고, 서버가 리스닝 중인지만 검사)
     */
    public boolean isVoiceServerReachable(String webSocketUrl) {
        if (webSocketUrl == null || webSocketUrl.isBlank()) {
            return false;
        }
        try {
            URI uri = parseWebSocketUri(webSocketUrl);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : ("https".equals(uri.getScheme()) ? 443 : 80);
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
                return true;
            }
        } catch (Exception e) {
            log.debug("음성 서버 연결 확인 실패: {} - {}", webSocketUrl, e.getMessage());
            return false;
        }
    }

    private static URI parseWebSocketUri(String webSocketUrl) throws URISyntaxException {
        String normalized = webSocketUrl.strip();
        if (normalized.startsWith("ws://")) {
            normalized = "http://" + normalized.substring(5);
        } else if (normalized.startsWith("wss://")) {
            normalized = "https://" + normalized.substring(6);
        }
        return new URI(normalized);
    }
}
