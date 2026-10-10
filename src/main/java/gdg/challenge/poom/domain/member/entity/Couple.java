package gdg.challenge.poom.domain.member.entity;

import gdg.challenge.poom.domain.member.entity.enums.CoupleStatus;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.CoupleErrorCode;
import gdg.challenge.poom.global.error.exception.handler.CoupleException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private LocalDateTime disconnectedAt;     // 연결 해제 시각

    private LocalDateTime deleteScheduledAt;  // 실제 삭제 예정 시각

    // member의 id 정렬을 통해 부부 중복 연결 방지
    public static Couple create(Member a, Member b) {
        Couple c = new Couple();
        boolean aFirst = a.getId() < b.getId();
        c.memberA = aFirst ? a : b;
        c.memberB = aFirst ? b : a;
        c.coupleStatus = CoupleStatus.CONNECTED;
        return c;
    }


    // 연결 해제 - 유예 기간 30일 설정
    public void requestDisconnect() {
        this.coupleStatus = CoupleStatus.DISCONNECTED_GRACE_PERIOD;
        this.disconnectedAt = LocalDateTime.now();
        this.deleteScheduledAt = this.disconnectedAt.plusDays(30);
    }

    // 연결 복구
    public void restore() {
        if (coupleStatus != CoupleStatus.DISCONNECTED_GRACE_PERIOD)
            throw new CoupleException(CoupleErrorCode.NOT_IN_DISCONNECTED_GRACE_PERIOD);
        if (LocalDateTime.now().isAfter(deleteScheduledAt)) {
            throw new CoupleException(CoupleErrorCode.DISCONNECTED_GRACE_PERIOD_EXPIRED);
        }

        this.coupleStatus = CoupleStatus.CONNECTED;
        this.disconnectedAt = null;
        this.deleteScheduledAt = null;
    }

    // 상대방 구하기
    public Member getPartnerOf(Long memberId) {
        if (memberA.getId().equals(memberId)) return memberB;
        if (memberB.getId().equals(memberId)) return memberA;
        throw new CoupleException(CoupleErrorCode.COUPLE_NOT_FOUND_BY_MEMBER);
    }
}
