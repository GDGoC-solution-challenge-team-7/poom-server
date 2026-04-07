package gdg.challenge.poom.domain.chat.service.query;

import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.repository.ChatMessageRepository;
import gdg.challenge.poom.domain.chat.repository.ChatRoomRepository;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatResponseDTO.ChatPreview> getChatRoomList(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return chatRoomRepository.findByMember(member).stream()
                .map(ChatConverter::toChatPreview)
                .toList();
    }

    public ChatResponseDTO.ChatRoomInfo getChatMessage(Long memberId, Long chatRoomId){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(0, 30);

        if (!chatRoom.getMember().getId().equals(memberId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom, pageRequest);
        return ChatConverter.toChatRoomMessage(chatRoom, chatMessages);
    }

    public ChatResponseDTO.ChatRoomSetting getChatRoomSettings(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return ChatConverter.toChatRoomSetting(member);
    }

}
