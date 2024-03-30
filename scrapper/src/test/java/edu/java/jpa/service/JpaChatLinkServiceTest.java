package edu.java.jpa.service;

import edu.java.domain.Chat;
import edu.java.domain.Link;
import edu.java.dto.ChatLinkDTO;
import edu.java.exception.ChatNotFoundException;
import edu.java.jpa.repository.ChatRepository;
import edu.java.jpa.repository.LinkRepository;
import edu.java.jpa.service.JpaChatLinkService;
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
@TestPropertySource(properties = {"app.database-access-type=jpa"})
public class JpaChatLinkServiceTest extends IntegrationTest {

    @Autowired
    private JpaChatLinkService chatLinkService;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ChatRepository chatRepository;

    private Chat testChat;
    private Link testLink;

    @BeforeEach
    void setup() {
        testChat = new Chat();
        testChat.setChatId(1L);
        testChat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(testChat);

        testLink = new Link();
        testLink.setUrl("http://example.com");
        testLink.setDescription("Example Description");
        testLink.setCreatedAt(LocalDateTime.now());
        linkRepository.save(testLink);
    }

    @Test
    @Transactional
    @Rollback
    void addLinkToChat_ThrowsChatNotFoundException_IfChatDoesNotExist() {
        final long nonExistingChatId = -1L;
        ChatNotFoundException thrown = assertThrows(
            ChatNotFoundException.class,
            () -> chatLinkService.addLinkToChat(nonExistingChatId, testLink.getLinkId())
        );

        assertTrue(thrown.getMessage().contains("Chat with ID " + nonExistingChatId + " not found."));
    }

    @Test
    @Transactional
    @Rollback
    void addLinkToChat_AddsLink_IfChatExists() {
        chatLinkService.addLinkToChat(testChat.getChatId(), testLink.getLinkId());
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChat.getChatId());
        assertTrue(links.stream().anyMatch(link -> link.getLinkId().equals(testLink.getLinkId())));
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkFromChat_RemovesLink() {
        chatLinkService.addLinkToChat(testChat.getChatId(), testLink.getLinkId());
        chatLinkService.removeLinkFromChat(testChat.getChatId(), testLink.getLinkId());
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChat.getChatId());
        assertTrue(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksForChat_ReturnsCorrectLinks() {
        chatLinkService.addLinkToChat(testChat.getChatId(), testLink.getLinkId());
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChat.getChatId());
        assertFalse(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsForLink_ReturnsCorrectChats() {
        chatLinkService.addLinkToChat(testChat.getChatId(), testLink.getLinkId());
        Collection<ChatLinkDTO> chats = chatLinkService.findAllChatsForLink(testLink.getLinkId());
        assertFalse(chats.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void existsChatsForLink_ReturnsTrue_WhenChatsExist() {
        chatLinkService.addLinkToChat(testChat.getChatId(), testLink.getLinkId());
        assertTrue(chatLinkService.existsChatsForLink(testLink.getLinkId()));
    }

    @Test
    @Transactional
    @Rollback
    void existsChatsForLink_ReturnsFalse_WhenNoChatsExist() {
        assertFalse(chatLinkService.existsChatsForLink(testLink.getLinkId()));
    }
}
