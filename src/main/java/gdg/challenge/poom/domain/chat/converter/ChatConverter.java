package gdg.challenge.poom.domain.chat.converter;

import com.google.genai.Chat;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;

import java.util.List;

public class ChatConverter {

    public static ChatResponseDTO.ChatPreview toChatPreview(ChatRoom chatRoom) {
        return ChatResponseDTO.ChatPreview.builder()
                .title(chatRoom.getTitle())
                .updatedAt(chatRoom.getUpdatedAt().toLocalDate())
                .build();
    }

    public static ChatResponseDTO.ReplyMessage toReplyMessage(String reply){
        return ChatResponseDTO.ReplyMessage.builder()
                .reply(reply)
                .build();
    }

    public static ChatResponseDTO.ChatMessage toChatMessage(ChatMessage chatMessage) {
        return ChatResponseDTO.ChatMessage.builder()
                .content(chatMessage.getContent())
                .senderType(chatMessage.getSenderType())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static ChatResponseDTO.ChatRoomInfo toChatRoomMessage(ChatRoom chatRoom, List<ChatMessage> chatMessages) {

        List<ChatResponseDTO.ChatMessage> chatMessageList = chatMessages.stream()
                .map(ChatConverter::toChatMessage)
                .toList();

        return ChatResponseDTO.ChatRoomInfo.builder()
                .chatMessageList(chatMessageList)
                .title(chatRoom.getTitle())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }

    public static ChatResponseDTO.ChatRoomSetting toChatRoomSetting(ChatRoom chatRoom) {
        return ChatResponseDTO.ChatRoomSetting.builder()
                .characterType(chatRoom.getCharacterType())
                .chatMode(chatRoom.getChatMode())
                .build();
    }
}
