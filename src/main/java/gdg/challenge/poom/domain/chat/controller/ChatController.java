package gdg.challenge.poom.domain.chat.controller;

import gdg.challenge.poom.domain.chat.dto.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "채팅 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 메시지 전송", description = "육아에 지친 산모들을 위한 AI 챗봇. style로 공감/해결 선택. 이미지는 imageUrl(S3 등)으로 전달 시 멀티모달 분석.")
    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        String userMessage = request != null ? request.message() : null;
        String style = request != null ? request.style() : null;
        String imageUrl = request != null ? request.imageUrl() : null;
        String response = chatService.chat(userMessage, style, imageUrl);
        return ResponseEntity.ok(new ChatResponseDTO(response));
    }
}
