package gdg.challenge.poom.domain.chat.service.command;


import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.domain.chat.repository.ChatMessageRepository;
import gdg.challenge.poom.domain.chat.repository.ChatRoomRepository;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public ChatRoom createChatRoom(Long memberId, String title, ChatRequestDTO.ChatMessageRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (request.chatRoomId() == null) {
            return chatRoomRepository.save(
                    ChatConverter.toChatRoom(member, title, request)
            );
        } else {
            return chatRoomRepository.findById(request.chatRoomId())
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        }
    }

    public ChatMessage createChatMessage(
            SenderType senderType, MessageType messageType,
            String content, String mediaUrl, ChatRoom chatRoom
     ){
        ChatMessage chatMessage = ChatConverter.toChatMessage(senderType, messageType, content, mediaUrl, chatRoom);
        return chatMessageRepository.save(chatMessage);
    }

    public ChatResponseDTO.ChatRoomSetting setChatRoomSetting(Long memberId, ChatRequestDTO.ChatRoomSetting request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.setChatRoomSetting(request.characterType(), request.chatMode());
        return ChatConverter.toChatRoomSetting(member);
    }

    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));


    }
}
