package gdg.challenge.poom.domain.voice.dto;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;

/**
 * 음성 세션 STT를 채팅 메시지로 저장할 때 요청 바디
 * 신규 방: chatRoomId null, role USER, characterType 필수.
 * AI 음성 전사: role AI, chatRoomId 필수.
 */
public record VoiceTranscriptRequest(
        Long chatRoomId,
        SenderType role,
        String text,
        CharacterType characterType
) {}
