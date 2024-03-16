package edu.java.dao;

import edu.java.dto.ChatLinkDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;


@Repository
public class JdbcChatLinkDao implements ChatLinkDao {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public JdbcChatLinkDao(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void add(ChatLinkDTO chatLink) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update(
                "INSERT INTO chat_link (chat_id, link_id, shared_at) VALUES (?, ?, ?)",
                chatLink.getChatId(), chatLink.getLinkId(), chatLink.getSharedAt()
            );
            return null;
        });
    }

    @Override
    public void remove(Long chatId, Long linkId) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);
            return null;
        });
    }

    @Override
    public List<ChatLinkDTO> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM chat_link",
            (rs, rowNum) -> new ChatLinkDTO(
                rs.getLong("chat_id"),
                rs.getLong("link_id"),
                rs.getTimestamp("shared_at").toLocalDateTime()
            )
        );
    }
}
