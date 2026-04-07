package gdg.challenge.poom.domain.member.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import gdg.challenge.poom.domain.chat.entity.enums.UploadDomain;
import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.global.data.GcsConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GcsService {

    private final GcsConfigData gcsConfigData;

    // 단건
    public MemberResponseDTO.SignedUrlResponse generateUploadSignedUrl(Long memberId, MemberRequestDTO.SignedUrlRequest request, Storage storage) {
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

    // 여러 건
    public MemberResponseDTO.SignedUrlBatchResponse generateUploadSignedUrl(
            Long memberId,
            MemberRequestDTO.SignedUrlBatchRequest request
    ) {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(gcsConfigData.getProjectId())
                .build()
                .getService();

        List<MemberResponseDTO.SignedUrlResponse> results = request.files().stream()
                .map(file -> generateUploadSignedUrl(memberId, file, storage))
                .toList();
        return MemberConverter.toSignedUrlResponse(results);
    }

    private String buildObjectName(Long memberId, UploadDomain domain, String filename) {
        String extension = extractExtension(filename);
        String uuid = UUID.randomUUID().toString();

        return switch (domain) {
            case PROFILE_IMAGE -> "member/profile/" + memberId + "/" + uuid + extension;
            case CHAT_IMAGE -> "chat/message/" + memberId + "/" + uuid + extension;
            case EXPERT_VERIFICATION -> "expert/verification/" + memberId + "/" + uuid + extension;
        };
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

}
