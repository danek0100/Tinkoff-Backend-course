package edu.java.dao;

import edu.java.dto.ChatLinkDTO;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JdbcChatLinkDaoTests extends IntegrationTest {

    @Autowired
    private JdbcChatLinkDao chatLinkDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long testChatId = 1L;
    private Long testLinkId;

    private final LocalDateTime sharedAt = LocalDateTime.now();

    @BeforeEach
    void setup() {
        jdbcTemplate.update("INSERT INTO chat (chat_id, created_at, created_by) VALUES (?, ?, ?)",
            testChatId, LocalDateTime.now(), "test_creator");

        testLinkId = addLinkAndReturnId("http://example.com", "Test description",
            LocalDateTime.now(), "test_creator");
    }

    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? OR link_id = ?", testChatId, testLinkId);
        jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", testChatId);
        jdbcTemplate.update("DELETE FROM link WHERE link_id = ?", testLinkId);
    }

    @Test
    @Transactional
    public void addTest() {
        ChatLinkDTO chatLink = new ChatLinkDTO(testChatId, testLinkId, sharedAt);
        chatLinkDao.add(chatLink);

        List<ChatLinkDTO> results = jdbcTemplate.query(
                "SELECT * FROM chat_link WHERE chat_id = ? AND link_id = ?",
                new Object[]{testChatId, testLinkId},
                (rs, rowNum) -> new ChatLinkDTO(
                        rs.getLong("chat_id"),
                        rs.getLong("link_id"),
                        rs.getTimestamp("shared_at").toLocalDateTime()
                )
        );

        assertFalse(results.isEmpty());
        ChatLinkDTO result = results.get(0);
        assertThat(result.getChatId()).isEqualTo(testChatId);
        assertThat(result.getLinkId()).isEqualTo(testLinkId);
        assertThat(result.getSharedAt().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(sharedAt.truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @Transactional
    public void removeTest() {
        ChatLinkDTO chatLink = new ChatLinkDTO(testChatId, testLinkId, sharedAt);
        chatLinkDao.add(chatLink);

        chatLinkDao.remove(testChatId, testLinkId);

        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_link WHERE chat_id = ? AND link_id = ?",
                new Object[]{testChatId, testLinkId},
                Integer.class
        );

        assertThat(count).isEqualTo(0);
    }

    @Test
    @Transactional
    public void findAllTest() {
        chatLinkDao.add(new ChatLinkDTO(testChatId, testLinkId, sharedAt));

        List<ChatLinkDTO> results = chatLinkDao.findAll();

        assertThat(results.size()).isGreaterThan(0);
    }

    public Long addLinkAndReturnId(String url, String description, LocalDateTime createdAt, String createdBy) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO link (url, description, created_at, created_by) VALUES (?, ?, ?, ?)",
                    new String[] {"link_id"} // Указываем, что ожидаем возврата link_id
                );
                ps.setString(1, url);
                ps.setString(2, description);
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(createdAt));
                ps.setString(4, createdBy);
                return ps;
            },
            keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
