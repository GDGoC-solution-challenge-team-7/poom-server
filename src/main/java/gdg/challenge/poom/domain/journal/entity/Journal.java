package gdg.challenge.poom.domain.journal.entity;

import gdg.challenge.poom.domain.journal.entity.enums.JournalEmotion;
import gdg.challenge.poom.domain.journal.entity.enums.Visibility;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.global.common.BaseEntity;
import gdg.challenge.poom.global.error.code.status.JournalErrorCode;
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
@Table(name = "journal",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_journal_member_date",
                columnNames = {"member_id", "journal_date"})
)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

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

    // 공개범위 변경 도메인 메서드
    public void changeVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

//    // 접근 가능 여부 판단 (핵심 로직)
//    public boolean canBeViewedBy(Member viewer) {
//        // 작성자 본인은 항상 가능
//        if (member.getId().equals(viewer.getId())) {
//            return true;
//        }
//        // 부부 공유일 때만 같은 커플이면 가능
//        return visibility == Visibility.COUPLE
//                && member.getCouple() != null
//                && viewer.getCouple() != null
//                && member.getCouple().getId().equals(viewer.getCouple().getId());
//    }

    public void addJournalImage(List<JournalImage> journalImageList) {
        if (journalImageList == null || journalImageList.isEmpty()) {
            throw new JournalException(JournalErrorCode.JOURNAL_IMAGE_REQUIRED);
        }

        this.journalImageList.addAll(journalImageList);
        for (JournalImage journalImage:journalImageList)
            journalImage.setJournal(this);
    }
}
