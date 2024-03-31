package edu.java.jdbc.service;

import edu.java.dao.ChatDao;
import edu.java.dto.ChatDTO;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.ChatService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JdbcChatService implements ChatService {

    private final ChatDao chatDao;

    @Override
    public void register(long chatId) {
        if (chatDao.existsById(chatId)) {
            throw new ChatAlreadyRegisteredException("Chat with id " + chatId + " already exists.");
        }
        chatDao.add(new ChatDTO(chatId, LocalDateTime.now()));
    }

    @Override
    public void unregister(long chatId) {
        if (!chatDao.existsById(chatId)) {
            throw new ChatNotFoundException("Chat with Id " + chatId + " not found.");
        }
        chatDao.remove(chatId);
    }
}
