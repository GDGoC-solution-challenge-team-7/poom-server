package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.Role;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import gdg.challenge.poom.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
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

//    private String phoneNumber;
//
//    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    private LocalDate childBirthDueDate;

    private LocalDate childBirthDate;

    @Enumerated(EnumType.STRING)
    private BirthRelationship birthRelationship;

    private String expertiseFile;

    private String deviceToken;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pushAlarm = true;

    private LocalTime dailyAlarmTime;

    private Boolean hasGivenBirth;

    public void changeMemberInfo(MemberRequestDTO.ChangeMemberInfo request){
        this.name = request.name();
        this.email = request.email();
        this.birthDate = request.birthDate();
        this.gender = request.gender();
        this.userType = request.userType();
        this.childBirthDueDate = request.childBirthDueDate();
        this.childBirthDate = request.childBirthDate();
        this.birthRelationship = request.birthRelationship();
        this.expertiseFile = request.expertiseFile();
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updateAlarmSetting(Boolean pushAlarm, LocalTime dailyAlarmTime) {
        this.pushAlarm = pushAlarm;
        this.dailyAlarmTime = dailyAlarmTime;
    }

}
