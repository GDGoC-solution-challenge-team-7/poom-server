package gdg.challenge.poom.domain.member.controller;

import gdg.challenge.poom.domain.member.dto.request.AlarmRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.AlarmResponseDTO;
import gdg.challenge.poom.domain.member.service.AlarmCommandService;
import gdg.challenge.poom.domain.member.service.AlarmQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
@Tag(name = "알림 API")
public class AlarmController {

    private final AlarmCommandService alarmCommandService;
    private final AlarmQueryService alarmQueryService;

    @Operation(summary = "디바이스 토큰 업데이트 API", description = "디바이스 토큰을 업데이트하는 API")
    @PatchMapping
    public ApiResponse<Void> updateDeviceToken(
            @RequestBody AlarmRequestDTO.UpdateDeviceToken request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        alarmCommandService.updateDeviceToken(request, customUserDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "알림 조회 API", description = "알림을 조회하는 API")
    @GetMapping
    public ApiResponse<AlarmResponseDTO.AlarmList> getAlarmList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        AlarmResponseDTO.AlarmList alarmList = alarmQueryService.getAlarmList(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(alarmList);
    }

    @Operation(summary = "알림 설정 조회 API", description = "알림 설정을 조회하는 API")
    @GetMapping("/settings")
    public ApiResponse<AlarmResponseDTO.AlarmSetting> getAlarmSettings(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        AlarmResponseDTO.AlarmSetting alarmSettings = alarmQueryService.getAlarmSettings(customUserDetails.getMemberId());
        return ApiResponse.onSuccess(alarmSettings);
    }

    @Operation(summary = "알림 설정 업데이트 API", description = "알림 설정을 업데이트하는 API")
    @PatchMapping("/settings")
    public ApiResponse<Void> updateAlarmSettings(AlarmRequestDTO.UpdateAlarmSetting request, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        alarmCommandService.updateAlarmSettings(request, customUserDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }
}
