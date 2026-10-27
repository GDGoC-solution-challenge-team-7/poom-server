package gdg.challenge.poom.global.common.controller;

import gdg.challenge.poom.global.data.AppLinkConfigData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// soom.asia 도메인 딥링크(https://soom.asia/{연결코드})를 앱으로 라우팅하기 위한
// Android App Link 소유권 검증 파일(assetlinks.json)을 제공한다.
@RestController
@RequiredArgsConstructor
@Tag(name = "Well-Known API")
public class WellKnownController {

    private final AppLinkConfigData appLinkConfigData;

    @Operation(summary = "Android App Link 검증", description = "assetlinks.json 반환")
    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> assetLinks() {
        AppLinkConfigData.Android android = appLinkConfigData.getAndroid();

        Map<String, Object> entry = Map.of(
                "relation", List.of("delegate_permission/common.handle_all_urls"),
                "target", Map.of(
                        "namespace", "android_app",
                        "package_name", android.getPackageName(),
                        "sha256_cert_fingerprints", android.getSha256CertFingerprints()
                )
        );

        return ResponseEntity.ok(List.of(entry));
    }
}
