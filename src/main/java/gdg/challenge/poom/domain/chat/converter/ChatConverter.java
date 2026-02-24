package gdg.challenge.poom.domain.chat.converter;

import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.domain.member.entity.Member;

import java.util.List;

public class ChatConverter {

    public static ChatResponseDTO.ChatPreview toChatPreview(ChatRoom chatRoom) {
        return ChatResponseDTO.ChatPreview.builder()
                .title(chatRoom.getTitle())
                .updatedAt(chatRoom.getUpdatedAt().toLocalDate())
                .build();
    }

    public static ChatResponseDTO.ReplyMessage toReplyMessage(String reply, Long chatRoomId){
        return ChatResponseDTO.ReplyMessage.builder()
                .reply(reply)
                .createRoomId(chatRoomId)
                .build();
    }

    public static ChatResponseDTO.ChatMessage toChatMessageDTO(ChatMessage chatMessage) {
        return ChatResponseDTO.ChatMessage.builder()
                .content(chatMessage.getContent())
                .senderType(chatMessage.getSenderType())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static ChatResponseDTO.ChatRoomInfo toChatRoomMessage(ChatRoom chatRoom, List<ChatMessage> chatMessages) {

        List<ChatResponseDTO.ChatMessage> chatMessageList = chatMessages.stream()
                .map(ChatConverter::toChatMessageDTO)
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

    public static ChatRoom toChatRoom(Member member, String title, ChatRequestDTO.ChatMessageRequest request){
        return ChatRoom.builder()
                .title(title)
                .chatMode(ChatMode.TEXT)
                .characterType(request.characterType())
                .member(member)
                .build();
    }

    public static ChatMessage toChatMessage(
            SenderType senderType, MessageType messageType,
            String content, String mediaUrl, ChatRoom chatRoom
    ){
        return ChatMessage.builder()
                .senderType(senderType)
                .messageType(messageType)
                .content(content)
                .mediaUrl(mediaUrl)
                .chatRoom(chatRoom)
                .build();
    }
}
