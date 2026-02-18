package gdg.challenge.poom.voice.controller;

import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.voice.dto.VoiceConnectionInfo;
import gdg.challenge.poom.voice.service.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 음성 비서 API.
 * 시연을 위해 WebSocket 연결 정보를 REST로 제공합니다.
 */
@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    @Operation(
            summary = "WebSocket 연결 정보 조회",
            description = "실시간 음성 비서(Gemini Live STS)에 연결할 WebSocket URL 등 시연용 연결 정보를 반환합니다. " +
                    "모바일 앱은 이 URL로 WebSocket 연결 후 음성 스트리밍을 진행합니다."
    )
    @GetMapping("/connection-info")
    public ResponseEntity<ApiResponse<VoiceConnectionInfo>> getConnectionInfo() {
        VoiceConnectionInfo info = voiceService.getConnectionInfo();
        return ResponseEntity.ok(ApiResponse.onSuccess(info));
    }
}
