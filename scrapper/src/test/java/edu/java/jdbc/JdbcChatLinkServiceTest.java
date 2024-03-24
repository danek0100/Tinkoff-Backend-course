package edu.java.jdbc;

import edu.java.dao.ChatDao;
import edu.java.dao.ChatLinkDao;
import edu.java.dao.LinkDao;
import edu.java.dto.ChatDTO;
import edu.java.dto.ChatLinkDTO;
import edu.java.dto.LinkDTO;
import edu.java.exception.ChatNotFoundException;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jdbc"})
public class JdbcChatLinkServiceTest extends IntegrationTest {

    @Autowired
    private JdbcChatLinkService chatLinkService;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private ChatLinkDao chatLinkDao;

    private final long testChatId = 1L;
    private long testLinkId;

    @BeforeEach
    void setup() {
        chatDao.add(new ChatDTO(testChatId, LocalDateTime.now()));

        LinkDTO link = LinkDTO.builder()
            .url("1")
            .description("TEST_DESCRIPTION")
            .createdAt(LocalDateTime.now())
            .build();

        testLinkId = linkDao.add(link);

        chatLinkDao.add(new ChatLinkDTO(testChatId, testLinkId, LocalDateTime.now()));
    }

    @Test
    @Transactional
    @Rollback
    void addLinkToChat_ThrowsChatNotFoundException_IfChatDoesNotExist() {
        Exception exception = assertThrows(ChatNotFoundException.class, () ->
            chatLinkService.addLinkToChat(999L, testLinkId));
        assertTrue(exception.getMessage().contains("Chat with ID 999 not found."));
    }

    @Test
    @Transactional
    @Rollback
    void addLinkToChat_AddsLink_IfChatExists() {
        LinkDTO secondLink = LinkDTO.builder()
            .url("2")
            .description("TEST_DESCRIPTION")
            .createdAt(LocalDateTime.now())
            .build();

        Long secondLinkId = linkDao.add(secondLink);

        chatLinkService.addLinkToChat(testChatId, secondLinkId);
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChatId);
        assertTrue(links.stream().anyMatch(link -> link.getLinkId() == testLinkId + 1));
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkFromChat_RemovesLink() {
        chatLinkService.removeLinkFromChat(testChatId, testLinkId);
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChatId);
        assertFalse(links.stream().anyMatch(link -> link.getLinkId() == testLinkId));
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksForChat_ReturnsCorrectLinks() {
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChatId);
        assertNotNull(links);
        assertFalse(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsForLink_ReturnsCorrectLinks() {
        Collection<ChatLinkDTO> links = chatLinkService.findAllChatsForLink(testLinkId);
        assertNotNull(links);
        assertFalse(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void existsChatsForLink_ReturnsTrue_WhenChatsExist() {
        boolean exists = chatLinkService.existsChatsForLink(testLinkId);
        assertTrue(exists);
    }

    @Test
    @Transactional
    @Rollback
    void existsChatsForLink_ReturnsFalse_WhenNoChatsExist() {
        chatLinkService.removeLinkFromChat(testChatId, testLinkId);
        boolean exists = chatLinkService.existsChatsForLink(testLinkId);
        assertFalse(exists);
    }
}
