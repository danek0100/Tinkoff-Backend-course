package edu.java.jdbc;

import edu.java.dao.ChatDao;
import edu.java.dao.ChatLinkDao;
import edu.java.dto.ChatLinkDTO;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.ChatLinkService;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class JdbcChatLinkService implements ChatLinkService {

    private final ChatLinkDao chatLinkDao;
    private final ChatDao chatDao;

    @Override
    public void addLinkToChat(long chatId, long linkId) throws ChatNotFoundException {
        if (!chatDao.existsById(chatId)) {
            throw new ChatNotFoundException("Chat with ID " + chatId + " not found.");
        }
        ChatLinkDTO chatLink = new ChatLinkDTO(chatId, linkId, LocalDateTime.now());
        chatLinkDao.add(chatLink);
    }

    @Override
    public void removeLinkFromChat(long chatId, long linkId) {
        chatLinkDao.remove(chatId, linkId);
    }

    @Override
    public Collection<ChatLinkDTO> findAllLinksForChat(long chatId) {
        return chatLinkDao.findAll().stream().filter(chatLinkDTO -> chatLinkDTO.getChatId().equals(chatId)).toList();
    }

    @Override
    public Collection<ChatLinkDTO> findAllChatsForLink(long linkId) {
        return chatLinkDao.getChatsForLink(linkId);
    }

    @Override
    public boolean existsChatsForLink(long linkId) {
        return !chatLinkDao.getChatsForLink(linkId).isEmpty();
    }
}
