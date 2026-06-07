package gdg.challenge.poom.domain.util;

import gdg.challenge.poom.domain.chat.entity.enums.UploadDomain;
import gdg.challenge.poom.global.error.code.status.GcsErrorCode;
import gdg.challenge.poom.global.error.exception.handler.GcsException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ImageUrlValidator {

    public void validate(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            validate(imageUrl);
        }
    }

    public void validate(String imageUrl) {
        boolean isValid = Arrays.stream(UploadDomain.values())
                .anyMatch(domain -> domain.matches(imageUrl));
        if (!isValid) {
            throw new GcsException(GcsErrorCode.INVALID_IMAGE_URL);
        }
    }

    // 특정 도메인만 허용
    public void validateByDomain(List<String> imageUrls, UploadDomain domain) {
        for (String imageUrl : imageUrls) {
            validateByDomain(imageUrl, domain);
        }
    }

    public void validateByDomain(String imageUrl, UploadDomain domain) {
        if (!domain.matches(imageUrl)) {
            throw new GcsException(GcsErrorCode.INVALID_IMAGE_URL);
        }
    }

}
