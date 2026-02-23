package gdg.challenge.poom.domain.chat.controller;

import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.service.ChatHelperService;
import gdg.challenge.poom.domain.chat.service.command.ChatCommandService;
import gdg.challenge.poom.domain.chat.service.query.ChatQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "채팅 API")
public class ChatController {

    private final ChatHelperService chatHelperService;
    private final ChatQueryService chatQueryService;
    private final ChatCommandService chatCommandService;

    @Operation(summary = "채팅 메시지 전송", description = "육아에 지친 산모들을 위한 AI 챗봇. style로 공감/해결 선택. 이미지는 imageUrl(S3 등)으로 전달 시 멀티모달 분석.")
    @PostMapping("/chat")
    public ApiResponse<ChatResponseDTO.ReplyMessage> chat(@RequestBody ChatRequestDTO.ChatMessageRequest request) {
        return ApiResponse.onSuccess(chatHelperService.chat(request));
    }

    @Operation(summary = "채팅방 리스트 조회 API", description = "참여한 채팅 리스트 조회 API")
    @GetMapping("/chat")
    public ApiResponse<List<ChatResponseDTO.ChatPreview>> getChatRoomList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<ChatResponseDTO.ChatPreview> chatRoomList = chatQueryService.getChatRoomList(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(chatRoomList);
    }

    @Operation(summary = "채팅 메시지 조회 API", description = "해당 채팅방의 메시지들을 최신순으로 조회하는 API")
    @GetMapping("/chat/{chatRoomId}")
    public ApiResponse<ChatResponseDTO.ChatRoomInfo> getChatMessageList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long chatRoomId
    ){
        ChatResponseDTO.ChatRoomInfo chatRoomInfo = chatQueryService.getChatMessage(customUserDetails.getMemberId(), chatRoomId);
        return ApiResponse.onSuccess(chatRoomInfo);
    }

    @Operation(summary = "채팅방 설정 변경 API", description = "해당 채팅방의 메시지들을 최신순으로 조회하는 API")
    @PatchMapping("/chat/{chatRoomId}")
    public ApiResponse<ChatResponseDTO.ChatRoomSetting> changeChatRoomSetting(
            @RequestBody ChatRequestDTO.ChatRoomSetting chatRoomSetting,
            @PathVariable Long chatRoomId
    ){
        ChatResponseDTO.ChatRoomSetting chatRoomSettingResponse = chatCommandService.setChatRoom(chatRoomId, chatRoomSetting);
        return ApiResponse.onSuccess(chatRoomSettingResponse);
    }

    @Operation(summary = "채팅방 설정 조회 API", description = "해당 채팅방의 메시지들을 최신순으로 조회하는 API")
    @GetMapping("/chat/{chatRoomId}/settings")
    public ApiResponse<ChatResponseDTO.ChatRoomSetting> getChatRoomSetting(
            @PathVariable Long chatRoomId
    ){
        ChatResponseDTO.ChatRoomSetting chatRoomSettings = chatQueryService.getChatRoomSettings(chatRoomId);
        return ApiResponse.onSuccess(chatRoomSettings);
    }

}
