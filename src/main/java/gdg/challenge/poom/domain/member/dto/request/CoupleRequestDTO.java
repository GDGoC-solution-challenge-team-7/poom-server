package gdg.challenge.poom.domain.member.dto.request;

public record CoupleRequestDTO() {

    public record CoupleCode(
            String coupleCode
    ){ }

    public record CoupleCodePreview(
            String code
    ){ }
}
