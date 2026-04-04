package gdg.challenge.poom.domain.member.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.global.data.GcsConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GcsService {

    private final GcsConfigData gcsConfigData;

    public MemberResponseDTO.SignedUrlResponse generateUploadSignedUrl(Long memberId, MemberRequestDTO.SignedUrlRequest request) {
        String objectName = "profiles/" + memberId + "/" + UUID.randomUUID() + extractExtension(request.filename());

        Storage storage = StorageOptions.newBuilder()
                .setProjectId(gcsConfigData.getProjectId())
                .build()
                .getService();

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

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

}
