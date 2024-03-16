package edu.java.dao;

import edu.java.dto.LinkDTO;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JdbcLinkTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao linkDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_URL = "http://localhost:8080/test";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_CREATED_BY = "Test Creator";

    @Test
    @Transactional
    @Rollback
    void addTest() {
        LinkDTO link = new LinkDTO(null, TEST_URL, TEST_DESCRIPTION, LocalDateTime.now(), TEST_CREATED_BY);
        linkDao.add(link);

        List<LinkDTO> links = jdbcTemplate.query(
                "SELECT * FROM link WHERE url = ?",
                new Object[]{TEST_URL},
                (rs, rowNum) -> new LinkDTO(
                        rs.getLong("link_id"),
                        rs.getString("url"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("created_by")
                ));

        assertFalse(links.isEmpty());
        assertThat(links.get(0).getUrl()).isEqualTo(TEST_URL);
        assertThat(links.get(0).getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(links.get(0).getCreatedBy()).isEqualTo(TEST_CREATED_BY);
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        LinkDTO link = new LinkDTO(null, TEST_URL, TEST_DESCRIPTION, LocalDateTime.now(), TEST_CREATED_BY);
        linkDao.add(link);

        // Получение ID добавленной ссылки
        Long linkId = jdbcTemplate.queryForObject(
                "SELECT link_id FROM link WHERE url = ?",
                new Object[]{TEST_URL},
                Long.class);

        assertNotNull(linkId);

        linkDao.remove(linkId);

        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM link WHERE link_id = ?",
                new Object[]{linkId},
                Integer.class);

        assertThat(count).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void findAllTest() {
        jdbcTemplate.update("DELETE FROM link");

        linkDao.add(new LinkDTO(null, TEST_URL, TEST_DESCRIPTION, LocalDateTime.now(), TEST_CREATED_BY));
        linkDao.add(new LinkDTO(null, TEST_URL + "2", TEST_DESCRIPTION + "2", LocalDateTime.now(), TEST_CREATED_BY + "2"));

        List<LinkDTO> links = linkDao.findAll();

        assertNotNull(links);
        assertThat(links.size()).isEqualTo(2);
    }
}
