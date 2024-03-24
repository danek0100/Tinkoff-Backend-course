package edu.java.jdbc;

import edu.java.dto.ChatDTO;
import edu.java.jdbc.JdbcChatDao;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JdbcChatDaoTests extends IntegrationTest {

    @Autowired
    private JdbcChatDao chatDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long newChatId = 1L;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final String createdBy = "Tester";

    @BeforeEach
    public void setup() {
        jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", newChatId);
    }

    @Test
    @Transactional
    public void addTest() {
        ChatDTO newChat = new ChatDTO(newChatId, createdAt);
        chatDao.add(newChat);

        ChatDTO insertedChat = jdbcTemplate.queryForObject(
                "SELECT * FROM chat WHERE chat_id = ?",
                new Object[]{newChatId},
                (rs, rowNum) -> new ChatDTO(
                        rs.getLong("chat_id"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                )
        );

        assertNotNull(insertedChat);
        assertEquals(newChatId, insertedChat.getChatId());
        assertTrue(Math.abs(createdAt.until(insertedChat.getCreatedAt(), ChronoUnit.SECONDS)) < 5);
    }

    @Test
    @Transactional
    public void removeTest() {
        ChatDTO newChat = new ChatDTO(newChatId, createdAt);
        chatDao.add(newChat);

        chatDao.remove(newChatId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat WHERE chat_id = ?",
                Integer.class, newChatId
        );

        assertEquals(0, count);
    }

    @Test
    @Transactional
    public void findAllTest() {
        ChatDTO chat1 = new ChatDTO(newChatId, createdAt);
        ChatDTO chat2 = new ChatDTO(newChatId + 1, createdAt.plusDays(1));
        chatDao.add(chat1);
        chatDao.add(chat2);

        List<ChatDTO> allChats = chatDao.findAll();

        assertTrue(allChats.size() >= 2);
        assertTrue(allChats.stream().anyMatch(chat -> chat.getChatId().equals(newChatId)));
        assertTrue(allChats.stream().anyMatch(chat -> chat.getChatId().equals(newChatId + 1)));
    }

    @Test
    @Transactional
    public void existsByIdTest() {
        boolean existsBeforeAdding = chatDao.existsById(newChatId);
        assertFalse(existsBeforeAdding, "Chat should not exist before adding");

        ChatDTO newChat = new ChatDTO(newChatId, createdAt);
        chatDao.add(newChat);

        boolean existsAfterAdding = chatDao.existsById(newChatId);
        assertTrue(existsAfterAdding, "Chat should exist after adding");

        chatDao.remove(newChatId);

        boolean existsAfterRemoving = chatDao.existsById(newChatId);
        assertFalse(existsAfterRemoving, "Chat should not exist after removing");
    }

}
