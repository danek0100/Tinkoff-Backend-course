package edu.java.jpa.service;

import edu.java.domain.Chat;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.jpa.repository.ChatRepository;
import edu.java.service.ChatService;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

public class JpaChatService implements ChatService {

    private final ChatRepository chatRepository;

    public JpaChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public void register(long chatId) {
        if (chatRepository.existsById(chatId)) {
            throw new ChatAlreadyRegisteredException("Chat with id " + chatId + " already exists.");
        }
        Chat chat = new Chat();
        chat.setChatId(chatId);
        chat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void unregister(long chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException("Chat with Id " + chatId + " not found.");
        }
        chatRepository.deleteById(chatId);
    }
}
