package edu.java.jdbc;

import edu.java.dao.ChatDao;
import edu.java.dto.ChatDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;


@Repository
public class JdbcChatDao implements ChatDao {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public JdbcChatDao(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void add(ChatDTO chat) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("INSERT INTO chat (chat_id, created_at) VALUES (?, ?)",
                    chat.getChatId(), chat.getCreatedAt());
            return null;
        });
    }

    @Override
    public void remove(Long chatId) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", chatId);
            return null;
        });
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
