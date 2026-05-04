package gdg.challenge.poom.domain.chat.dto.request;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatRequestDTO() {

    /**
     * characterType: empathy(기본), solution. 이미지: imageUrl(S3 등 http/https URL).
     */
    public record ChatMessageRequest(
            Long chatRoomId,
            String message,
            List<String> imageUrls
    ){}

    public record ChatRoomSetting(
            @NotNull
            CharacterType characterType,
            @NotNull
            ChatMode chatMode
    ){}



}
