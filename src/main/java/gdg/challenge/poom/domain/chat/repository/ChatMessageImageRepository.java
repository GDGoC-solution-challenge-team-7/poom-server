package gdg.challenge.poom.domain.chat.repository;

import gdg.challenge.poom.domain.chat.entity.ChatMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageImageRepository extends JpaRepository<ChatMessageImage, Long> {
    List<ChatMessageImage> saveAll(List<ChatMessageImage> chatMessageImages);
}
