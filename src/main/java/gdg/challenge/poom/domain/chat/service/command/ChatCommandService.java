package gdg.challenge.poom.domain.chat.service.command;


import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatMessageImage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.domain.chat.repository.ChatMessageImageRepository;
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

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;
    private final MemberRepository memberRepository;

    public ChatRoom createChatRoom(Long memberId, String title, ChatRequestDTO.ChatMessageRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (request.chatRoomId() == null) {
            ChatRoom chatRoom = ChatConverter.toChatRoom(member, title, request);
            member.addChatRoom(chatRoom);
            return chatRoomRepository.save(chatRoom);
        } else {
            return chatRoomRepository.findById(request.chatRoomId())
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        }
    }

    public ChatMessage createChatMessage(
            SenderType senderType, MessageType messageType,
            String content, ChatRoom chatRoom, Long memberId, List<String> imageUrls
     ){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        ChatMessage chatMessage = ChatConverter.toChatMessage(
                senderType, messageType, content, chatRoom, member.getCharacterType()
        );
        chatRoom.addChatMessage(chatMessage);
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        if (hasImages(imageUrls)) {
            chatMessage.changeMessageType(MessageType.TEXT_IMAGE);
            List<ChatMessageImage> chatMessageImages = ChatConverter.toChatMessageImages(imageUrls, chatMessage);
            savedChatMessage.addImage(chatMessageImages);
            chatMessageImageRepository.saveAll(chatMessageImages);
        }
        return savedChatMessage;
    }

    public ChatResponseDTO.ChatRoomSetting setChatRoomSetting(Long memberId, ChatRequestDTO.ChatRoomSetting request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.setChatRoomSetting(request.characterType(), request.chatMode());
        return ChatConverter.toChatRoomSetting(member);
    }

    public void deleteChatRoom(Long memberId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!Objects.equals(memberId, chatRoom.getMember().getId())) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_DELETE_DENIED);
        }
        chatMessageRepository.deleteByChatRoom(chatRoom);
        chatRoomRepository.delete(chatRoom);
    }

    private boolean hasImages(List<String> imageUrls){
        return imageUrls != null && !imageUrls.isEmpty();
    }
}
