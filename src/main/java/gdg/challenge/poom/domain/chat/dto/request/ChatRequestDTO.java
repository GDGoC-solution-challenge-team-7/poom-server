package gdg.challenge.poom.domain.chat.dto.request;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;

public record ChatRequestDTO() {

    /**
     * style: empathy(기본), solution. 이미지: imageUrl(S3 등 http/https URL).
     */
    public record ChatMessageRequest(
            String message,
            String style,
            String imageUrl
    ){}

    public record ChatRoomSetting(
            CharacterType characterType,
            ChatMode chatMode
    ){}



}
