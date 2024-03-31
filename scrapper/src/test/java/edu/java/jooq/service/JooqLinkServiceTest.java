package edu.java.jooq.service;

import edu.java.dto.LinkDTO;
import edu.java.exception.LinkNotFoundException;
import edu.java.scrapper.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import static edu.java.jooq.generated.Tables.LINK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jooq"})
public class JooqLinkServiceTest extends IntegrationTest {

    @Autowired
    private JooqLinkService linkService;

    @Autowired
    private DSLContext dslContext;

    private final String testUrl = "http://example.com/test";
    private final String testDescription = "Test Description";

    @BeforeEach
    void setup() {
        dslContext.deleteFrom(LINK)
                  .where(LINK.URL.eq(testUrl))
                  .execute();
    }

    @Test
    void add_CreatesNewLink_WhenLinkDoesNotExist() {
        LinkDTO addedLink = linkService.add(testUrl, testDescription);
        assertNotNull(addedLink);
        assertEquals(testUrl, addedLink.getUrl());
        assertEquals(testDescription, addedLink.getDescription());
    }

    @Test
    void update_UpdatesLinkSuccessfully() {
        LinkDTO addedLink = linkService.add(testUrl, testDescription);
        Long testLinkId = addedLink.getLinkId();

        String updatedDescription = "Updated Description";
        LinkDTO updatedLink = new LinkDTO(testLinkId, testUrl, updatedDescription, LocalDateTime.now(), null, null);
        linkService.update(updatedLink);

        LinkDTO foundLink = linkService.findById(testLinkId);
        assertNotNull(foundLink);
        assertEquals(updatedDescription, foundLink.getDescription());
    }

    @Test
    void findById_ReturnsCorrectLink() {
        LinkDTO addedLink = linkService.add(testUrl, testDescription);
        Long testLinkId = addedLink.getLinkId();

        LinkDTO foundLink = linkService.findById(testLinkId);
        assertNotNull(foundLink);
        assertEquals(testUrl, foundLink.getUrl());
        assertEquals(testDescription, foundLink.getDescription());
    }

    @Test
    void findByUrl_ReturnsCorrectLink() {
        linkService.add(testUrl, testDescription);

        LinkDTO foundLink = linkService.findByUrl(testUrl);
        assertNotNull(foundLink);
        assertEquals(testUrl, foundLink.getUrl());
        assertEquals(testDescription, foundLink.getDescription());
    }

    @Test
    void listAll_ReturnsAllLinks() {
        linkService.add(testUrl, testDescription);

        Collection<LinkDTO> allLinks = linkService.listAll();
        assertFalse(allLinks.isEmpty());
        assertTrue(allLinks.stream().anyMatch(link -> testUrl.equals(link.getUrl())));
    }

    @Test
    void findLinksToCheck_ReturnsLinksNotCheckedSince() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        linkService.add(testUrl, testDescription);

        Collection<LinkDTO> linksToCheck = linkService.findLinksToCheck(threshold);
        assertTrue(linksToCheck.isEmpty() || linksToCheck.stream().allMatch(link -> link.getLastCheckTime() == null || link.getLastCheckTime().isBefore(threshold)),
                   "Should return links not checked since given threshold");
    }
}
