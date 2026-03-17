package gdg.challenge.poom.domain.chat.controller;

import gdg.challenge.poom.domain.chat.dto.ChatRequest;
import gdg.challenge.poom.domain.chat.dto.ChatResponse;
import gdg.challenge.poom.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "채팅 메시지 전송", description = "육아에 지친 산모들을 위한 AI 챗봇. style로 공감/해결 선택. 이미지는 imageUrl(S3 등)으로 전달 시 멀티모달 분석. chatTitle은 <chat_title> 파싱 결과.")
    @PostMapping("/api/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String userMessage = request != null ? request.message() : null;
        String style = request != null ? request.style() : null;
        String imageUrl = request != null ? request.imageUrl() : null;
        ChatService.ChatResult result = chatService.chat(userMessage, style, imageUrl);
        return ResponseEntity.ok(new ChatResponse(result.reply(), result.chatTitle()));
    }
}
