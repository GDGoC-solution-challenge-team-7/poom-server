package gdg.challenge.poom.domain.chat.entity;

import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    /**
     * LLM이 첫 응답에서 내려주는 <chat_title>...</chat_title> 구간의 요약 제목.
     * 실제 답변 텍스트에서 파싱해 저장한다.
     */
    private String chatTitle;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CharacterType characterType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMode chatMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setChatRoomSetting(CharacterType characterType, ChatMode chatMode) {
        this.characterType = characterType;
        this.chatMode = chatMode;
    }

    public void updateChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
}
