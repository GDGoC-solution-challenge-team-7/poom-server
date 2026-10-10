package gdg.challenge.poom.domain.journal.controller;

import gdg.challenge.poom.domain.journal.dto.request.JournalRequestDTO;
import gdg.challenge.poom.domain.journal.dto.request.enums.JournalAuthor;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.service.command.JournalCommandService;
import gdg.challenge.poom.domain.journal.service.query.JournalQueryService;
import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RequiredArgsConstructor
@RequestMapping("/api/v1/journals")
@RestController
@Tag(name = "일기 API")
public class JournalController {

    private final JournalCommandService journalCommandService;
    private final JournalQueryService journalQueryService;

    @Operation(summary = "일기 생성 API", description = "일기 생성 API, 이미지 경로: member/journal")
    @PostMapping
    public ApiResponse<JournalResponseDTO.CreatedJournal> createJournal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody JournalRequestDTO.JournalRequest request
    ){
        JournalResponseDTO.CreatedJournal journal = journalCommandService.createJournal(customUserDetails.getMemberId(), request);
        return ApiResponse.onSuccess(journal);
    }

    @Operation(summary = "월별 일기 조회 API", description = "월별 일기 조회하는 API")
    @GetMapping("/calendar")
    public ApiResponse<JournalResponseDTO.JournalListByMonth> getCalendarByMonth(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ){
        JournalResponseDTO.JournalListByMonth calendar = journalQueryService.getCalendar(customUserDetails.getMemberId(), month);
        return ApiResponse.onSuccess(calendar);
    }

    @Operation(summary = "날짜별 일기 조회 API", description = "월별 일기 조회하는 API")
    @GetMapping("/{date}")
    public ApiResponse<JournalResponseDTO.JournalByDate> getJournalByDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        JournalResponseDTO.JournalByDate journalByDate = journalQueryService.getJournalByDate(customUserDetails.getMemberId(), date);
        return ApiResponse.onSuccess(journalByDate);
    }

    @Operation(summary = "일기 리스트 조회 API", description = "월별 일기 조회하는 API")
    @GetMapping
    public ApiResponse<JournalResponseDTO.JournalList> getJournalList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam JournalAuthor author
    ){
        JournalResponseDTO.JournalList journalByMonth = journalQueryService.getJournalByMonth(customUserDetails.getMemberId(), month, author);
        return ApiResponse.onSuccess(journalByMonth);
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

    @Operation(summary = "일기 공개 범위 수정 API", description = "일기 공개 범위 수정하는 API, 'PRIVATE'|'COUPLE'")
    @PatchMapping("/{journalId}/visibility")
    public ApiResponse<JournalResponseDTO.JournalChangedVisibility> updateJournalVisibility(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid JournalRequestDTO.JournalVisibilityRequest request,
            @PathVariable Long journalId
    ){
        JournalResponseDTO.JournalChangedVisibility journal = journalCommandService.updateJournalVisibility(customUserDetails.getMemberId(), journalId, request);
        return ApiResponse.onSuccess(journal);
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
