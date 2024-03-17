package edu.java.jdbc;

import edu.java.dao.ChatDao;
import edu.java.dto.ChatDTO;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JdbcChatServiceTest {

    @Autowired
    private JdbcChatService chatService;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final long testChatId = 1L;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", testChatId);
    }

    @Test
    @Transactional
    @Rollback
    void register_CreatesNewChat_WhenChatDoesNotExist() {
        assertDoesNotThrow(() -> chatService.register(testChatId));
        assertTrue(chatDao.existsById(testChatId));
    }

    @Test
    @Transactional
    @Rollback
    void register_ThrowsChatAlreadyRegisteredException_WhenChatExists() {
        chatDao.add(new ChatDTO(testChatId, LocalDateTime.now()));

        Exception exception = assertThrows(ChatAlreadyRegisteredException.class, () ->
            chatService.register(testChatId));
        assertTrue(exception.getMessage().contains("Chat with Id " + testChatId + " already exists."));
    }

    @Test
    @Transactional
    @Rollback
    void unregister_RemovesChat_WhenChatExists() {
        chatDao.add(new ChatDTO(testChatId, LocalDateTime.now()));
        assertDoesNotThrow(() -> chatService.unregister(testChatId));
        assertFalse(chatDao.existsById(testChatId));
    }

    @Test
    @Transactional
    @Rollback
    void unregister_ThrowsChatNotFoundException_WhenChatDoesNotExist() {
        Exception exception = assertThrows(ChatNotFoundException.class, () ->
            chatService.unregister(testChatId));
        assertTrue(exception.getMessage().contains("Chat with Id " + testChatId + " not found."));
    }
}
