package edu.java.jpa;

import edu.java.domain.Chat;
import edu.java.dto.ChatDTO;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.scrapper.IntegrationTest;
import java.time.LocalDateTime;
import edu.java.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jpa"})
public class JpaChatServiceTest extends IntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    private final long testChatId = 100L;

    @BeforeEach
    void setup() {
        chatRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    void register_CreatesNewChat_WhenChatDoesNotExist() {
        assertDoesNotThrow(() -> chatService.register(testChatId));
        assertTrue(chatRepository.existsById(testChatId));
    }

    @Test
    @Transactional
    @Rollback
    void register_ThrowsChatAlreadyRegisteredException_WhenChatExists() {
        Chat chat = new Chat();
        chat.setChatId(testChatId);
        chat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        Exception exception = assertThrows(ChatAlreadyRegisteredException.class, () ->
            chatService.register(testChatId));
        assertTrue(exception.getMessage().contains("Chat with id " + testChatId + " already exists."));
    }

    @Test
    @Transactional
    @Rollback
    void unregister_RemovesChat_WhenChatExists() {
        Chat chat = new Chat();
        chat.setChatId(testChatId);
        chat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        assertDoesNotThrow(() -> chatService.unregister(testChatId));
        assertFalse(chatRepository.existsById(testChatId));
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
