package edu.java.jpa.service;

import edu.java.dto.LinkDTO;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.jpa.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import java.time.LocalDateTime;
import java.util.Collection;
import edu.java.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jpa"})
public class JpaLinkServiceTest extends IntegrationTest {

    @Autowired
    private LinkService linkService;

    @Autowired
    private LinkRepository linkRepository;

    private final String testUrl = "http://example.com/test";
    private final String testDescription = "Test Description";

    @BeforeEach
    void setup() {
        linkRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    void add_ThrowsLinkAlreadyAddedException_WhenLinkExists() {
        linkService.add(testUrl, testDescription); // Add the link first time

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
        LinkDTO linkDTO = linkService.add(testUrl, testDescription);
        String updatedDescription = "Updated Description";
        linkDTO.setDescription(updatedDescription);
        assertDoesNotThrow(() -> linkService.update(linkDTO));

        LinkDTO foundLink = linkService.findById(linkDTO.getLinkId());
        assertEquals(updatedDescription, foundLink.getDescription());
    }

    @Test
    @Transactional
    @Rollback
    void findById_ReturnsCorrectLink() {
        LinkDTO addedLink = linkService.add(testUrl, testDescription);
        LinkDTO foundLink = linkService.findById(addedLink.getLinkId());
        assertNotNull(foundLink);
        assertEquals(testUrl, foundLink.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    void findByUrl_ReturnsCorrectLink() {
        linkService.add(testUrl, testDescription);
        LinkDTO foundLink = linkService.findByUrl(testUrl);
        assertNotNull(foundLink);
        assertEquals(testDescription, foundLink.getDescription());
    }

    @Test
    @Transactional
    @Rollback
    void listAll_ReturnsAllLinks() {
        linkService.add(testUrl, testDescription);
        Collection<LinkDTO> allLinks = linkService.listAll();
        assertFalse(allLinks.isEmpty());
        assertTrue(allLinks.stream().anyMatch(link -> link.getUrl().equals(testUrl)));
    }

    @Test
    @Transactional
    @Rollback
    void findLinksToCheck_ReturnsLinksNotCheckedSince() {
        linkService.add(testUrl, testDescription);
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        Collection<LinkDTO> linksToCheck = linkService.findLinksToCheck(threshold);
        assertFalse(linksToCheck.isEmpty());
        assertTrue(linksToCheck.stream().allMatch(link -> link.getLastCheckTime() == null || link.getLastCheckTime().isBefore(threshold)));
    }
}
