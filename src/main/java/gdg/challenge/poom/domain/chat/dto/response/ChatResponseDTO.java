package gdg.challenge.poom.domain.chat.dto.response;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ChatResponseDTO() {

    @Builder
    public record ReplyMessage(
            String reply,
            Long createRoomId
    ){}

    @Builder
    public record ChatPreview(
            Long chatRoomId,
            String title,
            LocalDate updatedAt
    ){}

    @Builder
    public record ChatMessage(
            String content,
            SenderType senderType,
            LocalDateTime createdAt
    ){}

    @Builder
    public record ChatRoomInfo(
            List<ChatResponseDTO.ChatMessage> chatMessageList,
            String title,
            LocalDateTime updatedAt
    ){}

    @Builder
    public record ChatRoomSetting(
            CharacterType characterType,
            ChatMode chatMode
    ){}

}
