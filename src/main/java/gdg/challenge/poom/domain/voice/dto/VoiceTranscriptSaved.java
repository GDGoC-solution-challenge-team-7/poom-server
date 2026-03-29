package gdg.challenge.poom.domain.voice.dto;

import lombok.Builder;

/** 음성 전사 1건 저장 후 채팅방 ID */
@Builder
public record VoiceTranscriptSaved(Long chatRoomId) {}
