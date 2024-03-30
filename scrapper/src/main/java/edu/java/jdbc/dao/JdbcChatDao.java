package edu.java.jdbc.dao;

import edu.java.dao.ChatDao;
import edu.java.dto.ChatDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@AllArgsConstructor
public class JdbcChatDao implements ChatDao {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void add(ChatDTO chat) {
        jdbcTemplate.update("INSERT INTO chat (chat_id, created_at) VALUES (?, ?)",
                chat.getChatId(), chat.getCreatedAt());
    }

    @Transactional
    @Override
    public void remove(Long chatId) {
        jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", chatId);
    }

    @Override
    public List<ChatDTO> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat", (rs, rowNum) -> new ChatDTO(
            rs.getLong("chat_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    @Override
    public boolean existsById(Long chatId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM chat WHERE chat_id = ?",
            new Object[]{chatId},
            Integer.class
        );
        return count != null && count > 0;
    }
}
