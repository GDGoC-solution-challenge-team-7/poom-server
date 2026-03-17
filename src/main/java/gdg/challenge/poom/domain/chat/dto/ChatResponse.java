package gdg.challenge.poom.domain.chat.dto;

/**
 * reply: 사용자에게 보여줄 응답 본문.
 * chatTitle: 채팅방 제목(요약). <chat_title> 파싱 결과.
 */
public record ChatResponse(String reply, String chatTitle) {}
