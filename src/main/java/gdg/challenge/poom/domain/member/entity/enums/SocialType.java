package gdg.challenge.poom.domain.member.entity.enums;

public enum SocialType {
    GOOGLE, KAKAO;

    public static SocialType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("socialType 값이 없습니다.");
        }
        try {
            return SocialType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 socialType 입니다: " + value);
        }
    }
}
