package gdg.challenge.poom.domain.chat.repository;

import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);

    // 최신순으로 첫 페이지
    List<ChatMessage> findByChatRoomOrderByCreatedAtDescIdDesc(ChatRoom chatRoom, Pageable pageable);

    // 커서 이후(더 과거) 데이터: (createdAt, id) 기준으로 "작은 것"만
    @Query("""
        select m from ChatMessage m where m.chatRoom = :chatRoom
          and (
               m.createdAt < :cursorCreatedAt
               or (m.createdAt = :cursorCreatedAt and m.id < :cursorId)
          )
        order by m.createdAt desc, m.id desc
    """)
    List<ChatMessage> findNextPage(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    void deleteByChatRoom(ChatRoom chatRoom);
}
