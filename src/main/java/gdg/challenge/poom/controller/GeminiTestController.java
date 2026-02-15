package gdg.challenge.poom.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공식 문서(GenerateTextFromTextInput) 방식으로 Gemini API를 실험하는 테스트 엔드포인트.
 * @see <a href="https://ai.google.dev/gemini-api/docs">Gemini API 문서</a>
 */
@RestController
public class GeminiTestController {

    private static final String DEFAULT_MODEL = "gemini-3-flash-preview";
    private static final String DEFAULT_PROMPT = "Explain how AI works in a few words";

    private final Client genAiClient;

    public GeminiTestController(Client genAiClient) {
        this.genAiClient = genAiClient;
    }

    @Operation(summary = "Gemini model response test", description = "generateContent(model, prompt, null) + response.text() 로 동작합니다.")
    @GetMapping("/api/test/gemini")
    public ResponseEntity<GeminiTestResponse> test(
            @RequestParam(required = false, defaultValue = DEFAULT_MODEL) String model,
            @RequestParam(required = false, defaultValue = DEFAULT_PROMPT) String prompt) {
        GenerateContentResponse response =
                genAiClient.models.generateContent(model, prompt, null);
        String text = response.text();
        return ResponseEntity.ok(new GeminiTestResponse(model, prompt, text));
    }

    public record GeminiTestResponse(String model, String prompt, String text) {}
}
