package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.entity.enums.CoupleStatus;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.CoupleErrorCode;
import gdg.challenge.poom.global.error.exception.handler.CoupleException;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "couple")
public class Couple extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "couple_id")
    private Long id;

    // 연결 코드 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_a_id")
    @Setter
    private Member memberA;

    // 연결 코드를 입력한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_b_id")
    @Setter
    private Member memberB;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CoupleStatus coupleStatus = CoupleStatus.CONNECTED;

    public void changeCoupleStatus(CoupleStatus newStatus) {
        this.coupleStatus = newStatus;
    }

    public Member getPartnerOf(Long memberId) {
        if (memberA.getId().equals(memberId)) {
            return memberB;
        }
        if (memberB.getId().equals(memberId)) {
            return memberA;
        }
        throw new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER);
    }
}
