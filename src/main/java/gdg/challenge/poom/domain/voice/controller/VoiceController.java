package gdg.challenge.poom.domain.voice.controller;

import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.domain.voice.dto.VoiceConnectionInfo;
import gdg.challenge.poom.domain.voice.dto.VoiceTranscriptRequest;
import gdg.challenge.poom.domain.voice.dto.VoiceTranscriptSaved;
import gdg.challenge.poom.domain.voice.service.VoiceService;
import gdg.challenge.poom.domain.voice.service.VoiceTranscriptService;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 음성 비서 API.
 * 시연을 위해 WebSocket 연결 정보를 REST로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/voice")
@RequiredArgsConstructor
@Tag(name = "음성 API")
public class VoiceController {

    private final VoiceService voiceService;
    private final VoiceTranscriptService voiceTranscriptService;

    @Operation(
            summary = "WebSocket 연결 정보 조회",
            description = "실시간 음성 봇(Gemini Live STS)에 연결할 WebSocket URL 등 시연용 연결 정보를 반환. " +
                    "모바일 앱은 이 URL로 WebSocket 연결 후 음성 스트리밍을 진행."
    )
    @GetMapping("/connection-info")
    public ResponseEntity<ApiResponse<VoiceConnectionInfo>> getConnectionInfo() {
        VoiceConnectionInfo info = voiceService.getConnectionInfo();
        return ResponseEntity.ok(ApiResponse.onSuccess(info));
    }

    @Operation(
            summary = "음성 세션 STT 저장",
            description = "음성 봇의 사용자/어시스턴트 전사를 채팅 메시지로 저장. "
                    + "소켓에서 호출. 채팅 목록과 동일한 스레드에 표시."
    )
    @PostMapping("/transcript")
    public ResponseEntity<ApiResponse<VoiceTranscriptSaved>> saveVoiceTranscript(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody VoiceTranscriptRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(
                        voiceTranscriptService.saveVoiceTranscript(customUserDetails.getMemberId(), request)
                )
        );
    }
}
