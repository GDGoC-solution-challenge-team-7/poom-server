package gdg.challenge.poom.controller;

import gdg.challenge.poom.service.ChatService;
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

    @Operation(summary = "채팅 메시지 전송", description = "육아에 지친 산모들을 위한 AI 챗봇 서버스입니다. style로 공감(기본)/해결 중 선택 가능.")
    @PostMapping("/api/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String userMessage = request != null ? request.message() : null;
        String style = request != null ? request.style() : null;
        String response = chatService.chat(userMessage, style);
        return ResponseEntity.ok(new ChatResponse(response));
    }

    /** style: empathy(공감 우선, 기본값), solution(해결·실천 우선) */
    public record ChatRequest(String message, String style) {}

    public record ChatResponse(String reply) {}
}
