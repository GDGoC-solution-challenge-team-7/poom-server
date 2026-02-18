package gdg.challenge.poom.chat.dto;

/**
 * style: empathy(기본), solution. 이미지: imageUrl(S3 등 http/https URL).
 */
public record ChatRequest(String message, String style, String imageUrl) {}
