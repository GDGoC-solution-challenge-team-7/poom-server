package gdg.challenge.poom.domain.chat.service.command;


import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.repository.ChatRoomRepository;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatCommandService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatResponseDTO.ChatRoomSetting setChatRoom(Long chatRoomId, ChatRequestDTO.ChatRoomSetting request) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.setChatRoomSetting(request.characterType(), request.chatMode());
        return ChatConverter.toChatRoomSetting(chatRoom);
    }
}
