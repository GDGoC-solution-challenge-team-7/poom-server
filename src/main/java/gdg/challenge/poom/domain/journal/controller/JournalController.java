package gdg.challenge.poom.domain.journal.controller;

import gdg.challenge.poom.domain.journal.dto.request.JournalRequestDTO;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.service.command.JournalCommandService;
import gdg.challenge.poom.domain.journal.service.query.JournalQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/journal")
@RestController
@Tag(name = "일기 API")
public class JournalController {

    private final JournalCommandService journalCommandService;
    private final JournalQueryService journalQueryService;

    @Operation(summary = "일기 생성 API", description = "일기 생성 API")
    @PostMapping
    public ApiResponse<JournalResponseDTO.CreatedJournal> createJournal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody JournalRequestDTO.JournalRequest request
    ){
        JournalResponseDTO.CreatedJournal journal = journalCommandService.createJournal(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(journal);
    }

    @Operation(summary = "월별 일기 조회 API", description = "월별 일기 조회하는 API")
    @GetMapping
    public ApiResponse<JournalResponseDTO.JournalList> getCalendar(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ){
        JournalResponseDTO.JournalList calendar = journalQueryService.getCalendar(customUserDetails.getMemberId(), year, month);
        return ApiResponse.onSuccess(calendar);
    }

    @Operation(summary = "일기 상세 조회 API", description = "일기 상세 조회하는 API")
    @GetMapping("/{journalId}")
    public ApiResponse<JournalResponseDTO.JournalDetail> getJournal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long journalId
    ){
        JournalResponseDTO.JournalDetail journal = journalQueryService.getJournal(customUserDetails.getMemberId(), journalId);
        return ApiResponse.onSuccess(journal);
    }

    @Operation(summary = "일기 수정 API", description = "일기 수정하는 API")
    @PatchMapping("/{journalId}")
    public ApiResponse<Void> updateJournal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody JournalRequestDTO.JournalRequest request,
            @PathVariable Long journalId
    ){
        journalCommandService.updateJournal(customUserDetails.getMemberId(), journalId, request);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "일기 삭제 API", description = "일기 삭제하는 API")
    @DeleteMapping("/{journalId}")
    public ApiResponse<Void> deleteJournal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long journalId
    ){
        journalCommandService.deleteJournal(customUserDetails.getMemberId(), journalId);
        return ApiResponse.onSuccess(null);
    }
}
