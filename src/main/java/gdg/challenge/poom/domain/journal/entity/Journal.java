package gdg.challenge.poom.domain.journal.entity;

import gdg.challenge.poom.domain.chat.entity.ChatMessageImage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.ChatErrorCode;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
import gdg.challenge.poom.global.error.exception.handler.ChatException;
import gdg.challenge.poom.global.error.exception.handler.JournalException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "journal")
public class Journal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_id")
    private Long id;

    // TODO: 날짜마다 일기 하나씩만 있도록 하기
    @Column(nullable = false)
    private LocalDate journalDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalEmotion journalEmotion;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalImage> journalImageList = new ArrayList<>();

    public void changeJournal(LocalDate journalDate, JournalEmotion journalEmotion, String content) {
        this.journalDate = journalDate;
        this.journalEmotion = journalEmotion;
        this.content = content;
    }

    public void addJournalImage(List<JournalImage> journalImageList) {
        if (journalImageList == null || journalImageList.isEmpty()) {
            throw new JournalException(JournalErrorCode.JOURNAL_IMAGE_REQUIRED);
        }

        this.journalImageList.addAll(journalImageList);
        for (JournalImage journalImage:journalImageList)
            journalImage.setJournal(this);
    }
}
