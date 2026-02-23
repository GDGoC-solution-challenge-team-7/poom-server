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

    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CharacterType characterType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMode ChatMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setChatRoomSetting(CharacterType characterType, ChatMode chatMode) {
        this.characterType = characterType;
        this.ChatMode = chatMode;
    }
}
