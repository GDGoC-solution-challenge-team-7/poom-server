package gdg.challenge.poom.domain.member.entity.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CoupleStatus {
    CONNECTED("연결됨"),
    DISCONNECTED_GRACE_PERIOD("연결 해제, 유예 기간 중");

    private final String description;
}
