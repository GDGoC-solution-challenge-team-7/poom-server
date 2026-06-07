package gdg.challenge.poom.domain.journal.service.command;

import gdg.challenge.poom.domain.chat.entity.enums.UploadDomain;
import gdg.challenge.poom.domain.journal.converter.JournalConverter;
import gdg.challenge.poom.domain.journal.dto.request.JournalRequestDTO;
import gdg.challenge.poom.domain.journal.dto.response.JournalResponseDTO;
import gdg.challenge.poom.domain.journal.entity.Journal;
import gdg.challenge.poom.domain.journal.entity.JournalImage;
import gdg.challenge.poom.domain.journal.repository.JournalImageRepository;
import gdg.challenge.poom.domain.journal.repository.JournalRepository;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.domain.util.ImageUrlValidator;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.JournalException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalCommandService {

    private final JournalRepository journalRepository;
    private final JournalImageRepository journalImageRepository;
    private final MemberRepository memberRepository;
    private final GcsService gcsService;
    private final ImageUrlValidator imageUrlValidator;

    public JournalResponseDTO.CreatedJournal createJournal(Long memberId, JournalRequestDTO.JournalRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Journal journal = JournalConverter.toJournal(request);
        member.addJournal(journal);

        if (request.imageUrls() != null && !request.imageUrls().isEmpty()){
            imageUrlValidator.validateByDomain(request.imageUrls(), UploadDomain.JOURNAL_IMAGE);
            gcsService.validateExists(request.imageUrls());
            List<JournalImage> journalImageList = JournalConverter.toJournalImage(request.imageUrls());
            journal.addJournalImage(journalImageList);
            journalImageRepository.saveAll(journalImageList);
        }
        return JournalConverter.toCreatedJournal(journal.getJournalDate(), journal.getJournalEmotion());
    }

    public void updateJournal(Long memberId, Long journalId, JournalRequestDTO.JournalRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_NOT_FOUND));

        if (journal.getMember().getId() != member.getId()){
            throw new JournalException(JournalErrorCode.JOURNAL_ACCESS_DENIED);
        }
        // 기존 이미지 삭제
        List<JournalImage> oldImages = journalImageRepository.findByJournal(journal);
        if (oldImages.isEmpty()) {
            List<String> oldKeys = oldImages.stream()
                    .map(JournalImage::getImageUrl)
                    .toList();
            journalImageRepository.deleteByJournal(journal);
            for (String key : oldKeys) {
                gcsService.deleteFile(key);
            }
        }
        // 요청 들어온 이미지 저장
        if (request.imageUrls() != null && !request.imageUrls().isEmpty()) {
            imageUrlValidator.validateByDomain(request.imageUrls(), UploadDomain.JOURNAL_IMAGE);
            gcsService.validateExists(request.imageUrls());
            List<JournalImage> imageUrls = request.imageUrls().stream()
                    .map(imageUrl -> JournalImage.builder()
                            .imageUrl(imageUrl)
                            .build()
                    )
                    .toList();
            journal.addJournalImage(imageUrls);
            journalImageRepository.saveAll(imageUrls);
        }
        journal.changeJournal(request.journalDate(), request.journalEmotion(), request.content());
    }
    public void deleteJournal(Long memberId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_NOT_FOUND));

        if (journal.getMember().getId() != member.getId()){
            throw new JournalException(JournalErrorCode.JOURNAL_ACCESS_DENIED);
        }
        List<JournalImage> journalImageList = journal.getJournalImageList();
        journalRepository.delete(journal);

        // gcs 삭제 처리
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        journalImageList.forEach(journalImage -> gcsService.deleteFile(journalImage.getImageUrl()));
                    }
                }
        );
    }
}
