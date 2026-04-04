package gdg.challenge.poom.domain.chat.repository;

import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndMemberId(Long chatRoomId, Long memberId);
    List<ChatRoom> findByMember(Member member);
}
