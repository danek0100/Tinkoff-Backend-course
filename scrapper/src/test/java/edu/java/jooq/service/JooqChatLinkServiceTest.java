package edu.java.jooq.service;

import edu.java.dto.ChatLinkDTO;
import edu.java.jooq.service.JooqChatLinkService;
import org.apache.kafka.test.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

import static edu.java.jooq.generated.tables.Chat.CHAT;
import static edu.java.jooq.generated.tables.ChatLink.CHAT_LINK;
import static edu.java.jooq.generated.tables.Link.LINK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.database-access-type=jooq"})
public class JooqChatLinkServiceTest implements IntegrationTest {
    @Autowired
    private JooqChatLinkService chatLinkService;

    @Autowired
    private DSLContext dslContext;

    private long testChatId = 10L;

    private long prepareTestData() {
        OffsetDateTime nowUTC = LocalDateTime.now().atOffset(ZoneOffset.UTC);

        long chatId = dslContext.insertInto(CHAT)
            .columns(CHAT.CHAT_ID, CHAT.CREATED_AT)
            .values(testChatId, nowUTC)
            .returning(CHAT.CHAT_ID)
            .fetchOne()
            .getChatId();

        long linkId = dslContext.insertInto(LINK, LINK.URL, LINK.DESCRIPTION, LINK.CREATED_AT)
            .values("http://example.com", "Example Description", OffsetDateTime.now(ZoneOffset.UTC))
            .returning(LINK.LINK_ID)
            .fetchOne()
            .getLinkId();

        return linkId;
    }

    @AfterEach
    void cleanup() {
        dslContext.deleteFrom(CHAT_LINK).execute();
        dslContext.deleteFrom(LINK).execute();
        dslContext.deleteFrom(CHAT).execute();
    }

    @Test
    void addLinkToChat_AddsLink_IfChatExists() {
        long testLinkId = prepareTestData();

        chatLinkService.addLinkToChat(testChatId, testLinkId);

        boolean exists = dslContext.fetchExists(
                dslContext.selectFrom(CHAT_LINK)
                        .where(CHAT_LINK.CHAT_ID.eq(testChatId))
                        .and(CHAT_LINK.LINK_ID.eq(testLinkId))
        );

        assertTrue(exists, "Link should be added to chat");
    }

    @Test
    void removeLinkFromChat_RemovesLink_IfLinkExists() {
        long testLinkId = prepareTestData();

        chatLinkService.addLinkToChat(testChatId, testLinkId);
        chatLinkService.removeLinkFromChat(testChatId, testLinkId);

        boolean exists = dslContext.fetchExists(
                dslContext.selectFrom(CHAT_LINK)
                        .where(CHAT_LINK.CHAT_ID.eq(testChatId))
                        .and(CHAT_LINK.LINK_ID.eq(testLinkId))
        );

        assertFalse(exists, "Link should be removed from chat");
    }

    @Test
    void findAllLinksForChat_ReturnsCorrectLinks() {
        long testLinkId = prepareTestData();

        chatLinkService.addLinkToChat(testChatId, testLinkId);
        Collection<ChatLinkDTO> links = chatLinkService.findAllLinksForChat(testChatId);

        assertFalse(links.isEmpty(), "Should return at least one link for the chat");
        assertTrue(links.stream().anyMatch(link -> link.getLinkId() == testLinkId),
                "Returned links should include the added link");
    }

    @Test
    void findAllChatsForLink_ReturnsCorrectChats() {
        long testLinkId = prepareTestData();

        chatLinkService.addLinkToChat(testChatId, testLinkId);
        Collection<ChatLinkDTO> chats = chatLinkService.findAllChatsForLink(testLinkId);

        assertFalse(chats.isEmpty(), "Should return at least one chat for the link");
        assertTrue(chats.stream().anyMatch(chat -> chat.getChatId() == testChatId),
                "Returned chats should include the chat where the link was added");
    }

    @Test
    void existsChatsForLink_ReturnsTrue_WhenChatsExist() {
        long testLinkId = prepareTestData();

        chatLinkService.addLinkToChat(testChatId, testLinkId);
        boolean exists = chatLinkService.existsChatsForLink(testLinkId);

        assertTrue(exists, "Should return true when chats exist for the link");
    }

    @Test
    void existsChatsForLink_ReturnsFalse_WhenNoChatsExist() {
        long testLinkId = prepareTestData();

        boolean exists = chatLinkService.existsChatsForLink(testLinkId);

        assertFalse(exists, "Should return false when no chats exist for the link");
    }

}
