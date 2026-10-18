package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.ChatMode;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.entity.enums.ActiveStatus;
import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.Role;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_member_couple_code",
                columnNames = "couple_code"
        )
})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    private LocalDate childBirthDate;

    private String deviceToken;

    private Boolean hasGivenBirth;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CharacterType characterType = CharacterType.EMPATHY;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ChatMode chatMode = ChatMode.VOICE;

    private String profileImage;

    @Column(name = "couple_code", length = 6, unique = true)
    private String coupleCode;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus = ActiveStatus.ACTIVE;

    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private AlarmSetting alarmSetting;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Social> socialList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alarm> alarmList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Journal> journalList = new ArrayList<>();

    public void changeMemberInfo(MemberRequestDTO.ChangeMemberInfo request){
        this.name = request.name();
        this.email = request.email();
        this.birthDate = request.birthDate();
        this.gender = request.gender();
        this.userType = request.userType();
        this.childBirthDate = request.childBirthDate();
    }

    // 계정 비활성화
    public void softDelete() {
        this.activeStatus = ActiveStatus.INACTIVE;
        this.deletedAt = LocalDateTime.now();
    }

    // 계정 복구
    public void restore() {
        //
        if (this.deletedAt == null) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_DELETED);
        }
        this.activeStatus = ActiveStatus.ACTIVE;
        this.deletedAt = null;
    }

    public boolean isNotRestorable(int graceDays) {
        return deletedAt != null
                && deletedAt.plusDays(graceDays).isAfter(LocalDateTime.now());
    }

    public void assignCoupleCode(String coupleCode){
        this.coupleCode = coupleCode;
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setChatRoomSetting(CharacterType characterType, ChatMode chatMode) {
        this.characterType = characterType;
        this.chatMode = chatMode;
    }

    public void updateProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public void addChatRoom(ChatRoom chatRoom){
        chatRoomList.add(chatRoom);
        chatRoom.setMember(this);
    }

    public void addSocial(Social social){
        socialList.add(social);
        social.setMember(this);
    }

    public void addAlarm(Alarm alarm){
        alarmList.add(alarm);
        alarm.setMember(this);
    }


    public void addJournal(Journal journal){
        journalList.add(journal);
        journal.setMember(this);
    }
}
