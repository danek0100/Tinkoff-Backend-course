package edu.java.jdbc.dao;

import edu.java.dto.LinkDTO;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jdbc"})
public class JdbcLinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao linkDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_URL = "http://localhost:8080/test";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Test
    @Transactional
    @Rollback
    void addTest() {
        LinkDTO link = LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(LocalDateTime.now())
            .build();
        linkDao.add(link);

        List<LinkDTO> links = jdbcTemplate.query(
                "SELECT * FROM link WHERE url = ?",
                new Object[]{TEST_URL},
                (rs, rowNum) -> new LinkDTO(
                        rs.getLong("link_id"),
                        rs.getString("url"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("last_check_time") != null
                            ? rs.getTimestamp("last_check_time").toLocalDateTime()
                            : null,
                        rs.getTimestamp("last_update_time") != null
                            ?  rs.getTimestamp("last_update_time").toLocalDateTime()
                            : null)
                );

        assertFalse(links.isEmpty());
        assertThat(links.get(0).getUrl()).isEqualTo(TEST_URL);
        assertThat(links.get(0).getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertNull(links.get(0).getLastCheckTime());
        assertNull(links.get(0).getLastUpdateTime());
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        LinkDTO link = LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(LocalDateTime.now())
            .build();
        linkDao.add(link);

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

        linkDao.add(
            LinkDTO.builder()
                .url(TEST_URL)
                .description(TEST_DESCRIPTION)
                .createdAt(LocalDateTime.now())
                .build()
        );
        linkDao.add(
            LinkDTO.builder()
                .url(TEST_URL + "2")
                .description(TEST_DESCRIPTION + "2")
                .createdAt(LocalDateTime.now())
                .build()
        );

        List<LinkDTO> links = linkDao.findAll();

        assertNotNull(links);
        assertThat(links.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    public void updateTest() {
        LocalDateTime now = LocalDateTime.now();
        LinkDTO testLink = LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(now)
            .lastCheckTime(now)
            .lastUpdateTime(now)
            .build();

        testLink.setLinkId(linkDao.add(testLink));

        String updatedUrl = "http://updated-example.com";
        String updatedDescription = "Updated Description";
        LocalDateTime updatedLastCheckTime = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedLastUpdateTime = LocalDateTime.now();

        testLink.setUrl(updatedUrl);
        testLink.setDescription(updatedDescription);
        testLink.setLastCheckTime(updatedLastCheckTime);
        testLink.setLastUpdateTime(updatedLastUpdateTime);
        linkDao.update(testLink);

        LinkDTO updatedLink = linkDao.findById(testLink.getLinkId());

        assertThat(updatedLink.getUrl()).isEqualTo(updatedUrl);
        assertThat(updatedLink.getDescription()).isEqualTo(updatedDescription);
        assertThat(updatedLink.getLastCheckTime()).isEqualToIgnoringSeconds(updatedLastCheckTime);
        assertThat(updatedLink.getLastUpdateTime()).isEqualToIgnoringSeconds(updatedLastUpdateTime);
    }

    @Test
    @Transactional
    @Rollback
    void existsByUrlTest() {
        assertFalse(linkDao.existsByUrl(TEST_URL));

        linkDao.add(LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(LocalDateTime.now())
            .build());

        assertTrue(linkDao.existsByUrl(TEST_URL));
    }

    @Test
    @Transactional
    @Rollback
    void findLinksNotCheckedSinceTest() {
        LocalDateTime now = LocalDateTime.now();
        LinkDTO link = LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(now)
            .lastCheckTime(now)
            .build();

        link.setLinkId(linkDao.add(link));
        linkDao.update(link); // Setting new lastUpdateTime

        List<LinkDTO> linksNotChecked = linkDao.findLinksNotCheckedSince(now.minusDays(1));
        assertTrue(linksNotChecked.isEmpty());

        List<LinkDTO> linksChecked = linkDao.findLinksNotCheckedSince(now.plusDays(1));
        assertFalse(linksChecked.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findByIdTest() {
        Long linkId = linkDao.add(LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(LocalDateTime.now())
            .build());

        LinkDTO link = linkDao.findById(linkId);
        assertNotNull(link);
        assertThat(link.getUrl()).isEqualTo(TEST_URL);
        assertThat(link.getDescription()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    @Transactional
    @Rollback
    void findByUrlTest() {
        linkDao.add(LinkDTO.builder()
            .url(TEST_URL)
            .description(TEST_DESCRIPTION)
            .createdAt(LocalDateTime.now())
            .build());

        LinkDTO link = linkDao.findByUrl(TEST_URL);
        assertNotNull(link);
        assertThat(link.getDescription()).isEqualTo(TEST_DESCRIPTION);
    }

}
