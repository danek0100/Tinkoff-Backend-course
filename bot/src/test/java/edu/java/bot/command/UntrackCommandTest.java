package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import edu.java.bot.exception.ChatNotFoundException;
import edu.java.bot.exception.LinkNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UntrackCommandTest {

    @Mock
    private ScrapperApiClient scrapperApiClient;

    @InjectMocks
    private UntrackCommand command;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUntrackCommandResponseSuccess() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToUntrack = "https://example.com";
        LinkResponse linkResponse = new LinkResponse(1L, urlToUntrack, "Description");

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/untrack " + urlToUntrack);
        when(scrapperApiClient.removeLink(anyLong(), any(RemoveLinkRequest.class))).thenReturn(linkResponse);

        SendMessage response = command.handle(update);

        assertEquals("Ссылка удалена из отслеживания: " + urlToUntrack, response.getParameters().get("text"));
    }

    @Test
    void testUntrackCommandLinkNotFoundException() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToUntrack = "https://example.com";

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/untrack " + urlToUntrack);
        when(scrapperApiClient.removeLink(anyLong(), any(RemoveLinkRequest.class))).thenThrow(new LinkNotFoundException("Link not found"));

        SendMessage response = command.handle(update);

        assertEquals("Эта ссылка не отслеживается.", response.getParameters().get("text"));
    }

    @Test
    void testUntrackCommandChatNotFoundException() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToUntrack = "https://example.com";

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/untrack " + urlToUntrack);
        when(scrapperApiClient.removeLink(anyLong(), any(RemoveLinkRequest.class))).thenThrow(new ChatNotFoundException("Chat not found"));

        SendMessage response = command.handle(update);

        assertEquals("Необходима регистрация! Выполните /start.", response.getParameters().get("text"));
    }
}
