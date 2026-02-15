package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.entity.enums.Role;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    private Role role;

    private SocialType socialType;

    private String socialId;
}
