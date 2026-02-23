package gdg.challenge.poom.domain.chat.repository;

import gdg.challenge.poom.domain.chat.entity.ChatMessage;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
