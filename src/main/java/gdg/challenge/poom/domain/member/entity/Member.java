package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.Role;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
