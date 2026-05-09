package gdg.challenge.poom.domain.chat.entity;


import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    /** 음성 봇 전사·긴 답변 등 — VARCHAR(255)면 DB truncation 발생 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CharacterType characterType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder.Default
    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageImage> chatMessageImages = new ArrayList<>();

    public void changeMessageType(MessageType newMessageType) {
        this.messageType = newMessageType;
    }

    public void addImage(List<ChatMessageImage> chatMessageImages) {
        if (chatMessageImages == null || chatMessageImages.isEmpty()) {
            throw new ChatException(ChatErrorCode.CHAT_MESSAGE_IMAGE_REQUIRED);
        }

        this.chatMessageImages.addAll(chatMessageImages);
        for (ChatMessageImage chatMessageImage:chatMessageImages)
            chatMessageImage.setChatMessage(this);
    }
}
