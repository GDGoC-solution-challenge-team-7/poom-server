package gdg.challenge.poom.domain.util;

import gdg.challenge.poom.domain.chat.repository.ChatMessageImageRepository;
import gdg.challenge.poom.domain.journal.repository.JournalImageRepository;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MemberFileCollector {
    private final JournalImageRepository journalImageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;
    private final MemberRepository memberRepository;

    public List<String> collectByMemberId(Long memberId) {
        List<String> urls = new ArrayList<>();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        urls.addAll(journalImageRepository.findImageUrlsByMemberId(memberId));
        urls.addAll(chatMessageImageRepository.findImageUrlsByMemberId(memberId));
        urls.add(member.getProfileImage());

        return urls.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

}
