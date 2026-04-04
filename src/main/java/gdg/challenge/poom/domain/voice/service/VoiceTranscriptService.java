package gdg.challenge.poom.domain.voice.service;

import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.domain.chat.repository.ChatRoomRepository;
import gdg.challenge.poom.domain.chat.service.command.ChatCommandService;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.voice.dto.VoiceTranscriptRequest;
import gdg.challenge.poom.domain.voice.dto.VoiceTranscriptSaved;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.code.status.GeneralErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoiceTranscriptService {

    private static final String PROVISIONAL_TITLE = "새 대화";

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatCommandService chatCommandService;

    public VoiceTranscriptSaved saveVoiceTranscript(Long memberId, VoiceTranscriptRequest request) {
        if (request.text() == null || request.text().isBlank()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        if (request.role() == SenderType.USER) {
            return saveUserTranscript(memberId, request);
        }
        if (request.role() == SenderType.AI) {
            return saveAiTranscript(memberId, request);
        }
        throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
    }

    private VoiceTranscriptSaved saveUserTranscript(Long memberId, VoiceTranscriptRequest request) {
        String content = request.text().trim();
        if (request.chatRoomId() == null) {
            if (request.characterType() == null) {
                throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
            }
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            ChatRoom room = chatRoomRepository.save(
                    ChatRoom.builder()
                            .chatTitle(PROVISIONAL_TITLE)
//                            .chatMode(ChatMode.VOICE)
//                            .characterType(request.characterType())
                            .member(member)
                            .build()
            );
            chatCommandService.createChatMessage(SenderType.USER, MessageType.TEXT, content, null, room);
            return VoiceTranscriptSaved.builder()
                    .chatRoomId(room.getId())
                    .build();
        }
        ChatRoom room = resolveOwnedRoom(memberId, request.chatRoomId());
        chatCommandService.createChatMessage(SenderType.USER, MessageType.TEXT, content, null, room);
        return VoiceTranscriptSaved.builder()
                .chatRoomId(room.getId())
                .build();
    }

    private VoiceTranscriptSaved saveAiTranscript(Long memberId, VoiceTranscriptRequest request) {
        if (request.chatRoomId() == null) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }
        ChatRoom room = resolveOwnedRoom(memberId, request.chatRoomId());
        String content = request.text().trim();
        chatCommandService.createChatMessage(SenderType.AI, MessageType.TEXT, content, null, room);
        return VoiceTranscriptSaved.builder()
                .chatRoomId(room.getId())
                .build();
    }

    private ChatRoom resolveOwnedRoom(Long memberId, Long chatRoomId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (!room.getMember().getId().equals(memberId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        return room;
    }
}
