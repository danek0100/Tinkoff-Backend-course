package edu.java.jpa;

import edu.java.domain.ChatLink;
import edu.java.domain.ChatLinkId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ChatLinkRepository extends JpaRepository<ChatLink, ChatLinkId> {

    List<ChatLink> findByLinkId(Long linkId);

    List<ChatLink> findByChatId(Long chatId);

    boolean existsByLinkId(Long linkId);

    @Transactional
    void deleteByChatIdAndLinkId(Long chatId, Long linkId);
}
