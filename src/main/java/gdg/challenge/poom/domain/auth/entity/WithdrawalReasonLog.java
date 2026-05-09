package gdg.challenge.poom.domain.auth.entity;

import gdg.challenge.poom.domain.auth.entity.enums.WithdrawalReason;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "withdrawal_reason_log")
public class WithdrawalReasonLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_reason_log_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalReason reason;

    private String detail;
}
