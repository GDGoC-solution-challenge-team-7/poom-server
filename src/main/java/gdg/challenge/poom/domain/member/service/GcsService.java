package gdg.challenge.poom.domain.member.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import gdg.challenge.poom.domain.chat.entity.enums.UploadDomain;
import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.global.data.GcsConfigData;
import gdg.challenge.poom.global.error.code.status.GcsErrorCode;
import gdg.challenge.poom.global.error.exception.handler.GcsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsService {

    private final GcsConfigData gcsConfigData;
    private final Storage storage;

    // 단건 업로드
    public MemberResponseDTO.SignedUrlResponse generateUploadSignedUrl(Long memberId, MemberRequestDTO.SignedUrlRequest request) {
        validateFileType(request.domain(), request.contentType());
        String objectName = buildObjectName(memberId, request.domain(), request.filename());

        BlobInfo blobInfo = BlobInfo.newBuilder(gcsConfigData.getStorage().getBucket(), objectName)
                .setContentType(request.contentType())
                .build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                10, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.withContentType()
        );
        String publicUrl = "https://storage.googleapis.com/" + gcsConfigData.getStorage().getBucket() + "/" + objectName;
        return MemberConverter.toSignedUrlResponse(objectName, signedUrl.toString(), publicUrl);
    }

    // 여러 건 업로드
    public MemberResponseDTO.SignedUrlBatchResponse generateUploadSignedUrl(
            Long memberId,
            MemberRequestDTO.SignedUrlBatchRequest request
    ) {

        List<MemberResponseDTO.SignedUrlResponse> results = request.files().stream()
                .map(file -> generateUploadSignedUrl(memberId, file))
                .toList();
        return MemberConverter.toSignedUrlResponse(results);
    }

    public void deleteFile(String objectKey) {
        boolean deleted = storage.delete(gcsConfigData.getStorage().getBucket(), objectKey);

        if (!deleted) {
            throw new GcsException(GcsErrorCode.FILE_DELETE_FAILED);
        }
    }

    // 다운로드 URL 생성
    public String generateDownloadSignedUrl(String objectName) {

        BlobInfo blobInfo = BlobInfo.newBuilder(
                gcsConfigData.getStorage().getBucket(),
                objectName
        ).build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                10, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );

        return signedUrl.toString();
    }

    private String buildObjectName(Long memberId, UploadDomain domain, String filename) {
        String extension = extractExtension(filename);
        String uuid = UUID.randomUUID().toString();

        return switch (domain) {
            case PROFILE_IMAGE -> "member/profile/" + memberId + "/" + uuid + extension;
            case CHAT_IMAGE -> "chat/message/" + memberId + "/" + uuid + extension;
            case EXPERT_VERIFICATION -> "expert/verification/" + memberId + "/" + uuid + extension;
            case JOURNAL_IMAGE -> "member/journal/" + memberId + "/" + uuid + extension;
        };
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private void validateFileType(UploadDomain domain, String contentType) {
        switch (domain) {
            case PROFILE_IMAGE, CHAT_IMAGE, JOURNAL_IMAGE -> {
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new GcsException(GcsErrorCode.INVALID_IMAGE_FILE);
                }
            }
            case EXPERT_VERIFICATION -> {
                if (!"application/pdf".equals(contentType)) {
                    throw new GcsException(GcsErrorCode.INVALID_EXPERT_PDF_FILE);
                }
            }
        }
    }

}
