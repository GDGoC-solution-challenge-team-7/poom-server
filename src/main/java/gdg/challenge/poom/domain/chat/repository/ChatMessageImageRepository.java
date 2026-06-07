package gdg.challenge.poom.domain.chat.repository;

import gdg.challenge.poom.domain.chat.entity.ChatMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageImageRepository extends JpaRepository<ChatMessageImage, Long> {
    @Query("""
    select cmi.imageUrl
    from ChatMessageImage cmi
    where cmi.chatMessage.chatRoom.member.id = :memberId
    """)
    List<String> findImageUrlsByMemberId(Long memberId);
}
