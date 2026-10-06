package gdg.challenge.poom.domain.auth.dto.util;

public record EventDTO() {

    public record SignUpCompletedEvent(
            Long memberId
    ){}
}
