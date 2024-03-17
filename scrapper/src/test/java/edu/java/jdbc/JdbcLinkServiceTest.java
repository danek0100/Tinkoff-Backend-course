package edu.java.jdbc;

import edu.java.dto.LinkDTO;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JdbcLinkServiceTest extends IntegrationTest {

    @Autowired
    private JdbcLinkService linkService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String testUrl = "http://example.com/test";
    private final String testDescription = "Test Description";
    private Long testLinkId;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM link WHERE url = ?", testUrl);
        LinkDTO link = new LinkDTO(null, testUrl, testDescription, LocalDateTime.now(), null, null);
        testLinkId = linkService.add(testUrl, testDescription).getLinkId();
    }

    @Test
    @Transactional
    @Rollback
    void add_ThrowsLinkAlreadyAddedException_WhenLinkExists() {
        assertThrows(LinkAlreadyAddedException.class, () -> {
            linkService.add(testUrl, "Another Description");
        });
    }

    @Test
    @Transactional
    @Rollback
    void remove_ThrowsLinkNotFoundException_WhenLinkNotExists() {
        assertThrows(LinkNotFoundException.class, () -> {
            linkService.remove("http://nonexistent.com");
        });
    }

    @Test
    @Transactional
    @Rollback
    void update_UpdatesLinkSuccessfully() {
        String updatedDescription = "Updated Description";
        LinkDTO updatedLink = new LinkDTO(testLinkId, testUrl, updatedDescription, LocalDateTime.now(), null, null);
        assertDoesNotThrow(() -> linkService.update(updatedLink));

        LinkDTO foundLink = linkService.findById(testLinkId);
        assertEquals(updatedDescription, foundLink.getDescription());
    }

    @Test
    @Transactional
    @Rollback
    void findById_ReturnsCorrectLink() {
        LinkDTO foundLink = linkService.findById(testLinkId);
        assertNotNull(foundLink);
        assertEquals(testUrl, foundLink.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    void findByUrl_ReturnsCorrectLink() {
        LinkDTO foundLink = linkService.findByUrl(testUrl);
        assertNotNull(foundLink);
        assertEquals(testDescription, foundLink.getDescription());
    }

    @Test
    @Transactional
    @Rollback
    void listAll_ReturnsAllLinks() {
        Collection<LinkDTO> allLinks = linkService.listAll();
        assertFalse(allLinks.isEmpty());
        assertTrue(allLinks.stream().anyMatch(link -> link.getUrl().equals(testUrl)));
    }

    @Test
    @Transactional
    @Rollback
    void findLinksToCheck_ReturnsLinksNotCheckedSince() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        Collection<LinkDTO> linksToCheck = linkService.findLinksToCheck(threshold);
        assertFalse(linksToCheck.isEmpty());
        assertTrue(linksToCheck.stream().allMatch(link -> link.getLastCheckTime() == null || link.getLastCheckTime().isBefore(threshold)));
    }
}
