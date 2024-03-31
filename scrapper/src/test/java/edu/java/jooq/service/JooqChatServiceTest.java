package edu.java.jooq.service;

import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.scrapper.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.jooq.generated.Tables.CHAT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jooq"})
public class JooqChatServiceTest extends IntegrationTest {

    @Autowired
    private JooqChatService chatService;

    @Autowired
    private DSLContext dslContext;

    private final long testChatId = 1L;

    @BeforeEach
    void setup() {
        dslContext.deleteFrom(CHAT)
                  .where(CHAT.CHAT_ID.eq(testChatId))
                  .execute();
    }

    @Test
    void register_CreatesNewChat_WhenChatDoesNotExist() {
        assertDoesNotThrow(() -> chatService.register(testChatId));

        assertTrue(dslContext.fetchExists(
            dslContext.selectOne()
                      .from(CHAT)
                      .where(CHAT.CHAT_ID.eq(testChatId))
        ), "Chat should be registered successfully");
    }

    @Test
    void register_ThrowsChatAlreadyRegisteredException_WhenChatExists() {
        chatService.register(testChatId);

        Exception exception = assertThrows(ChatAlreadyRegisteredException.class, () ->
            chatService.register(testChatId));
        assertTrue(exception.getMessage().contains("Chat with id " + testChatId + " already exists."),
                   "Should throw ChatAlreadyRegisteredException");
    }

    @Test
    void unregister_RemovesChat_WhenChatExists() {
        chatService.register(testChatId);

        assertDoesNotThrow(() -> chatService.unregister(testChatId));

        assertFalse(dslContext.fetchExists(
            dslContext.selectOne()
                      .from(CHAT)
                      .where(CHAT.CHAT_ID.eq(testChatId))
        ), "Chat should be unregistered successfully");
    }

    @Test
    void unregister_ThrowsChatNotFoundException_WhenChatDoesNotExist() {
        Exception exception = assertThrows(ChatNotFoundException.class, () ->
            chatService.unregister(testChatId));
        assertTrue(exception.getMessage().contains("Chat with Id " + testChatId + " not found."),
                   "Should throw ChatNotFoundException");
    }
}
