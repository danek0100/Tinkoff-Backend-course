package edu.java.dao;

import edu.java.dto.LinkDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;


@Repository
public class JdbcLinkDao implements LinkDao {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public JdbcLinkDao(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void add(LinkDTO link) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update(
                "INSERT INTO link (url, description, created_at, created_by) VALUES (?, ?, ?, ?)",
                link.getUrl(), link.getDescription(), link.getCreatedAt(), link.getCreatedBy()
            );
            return null;
        });
    }

    @Override
    public void remove(Long linkId) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("DELETE FROM link WHERE link_id = ?", linkId);
            return null;
        });
    }

    @Override
    public List<LinkDTO> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            (rs, rowNum) -> new LinkDTO(
                rs.getLong("link_id"),
                rs.getString("url"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("created_by")
            )
        );
    }
}
