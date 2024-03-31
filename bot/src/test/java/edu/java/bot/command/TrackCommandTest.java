package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.exception.LinkAlreadyAddedException;
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

class TrackCommandTest {

    @Mock
    private ScrapperApiClient scrapperApiClient;

    @InjectMocks
    private TrackCommand command;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTrackCommandResponseSuccess() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToTrack = "https://example.com";
        Long chatId = 123L;
        LinkResponse mockLinkResponse = new LinkResponse(1L, urlToTrack, "Description");

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        when(message.text()).thenReturn("/track " + urlToTrack);
        when(scrapperApiClient.addLink(anyLong(), any(AddLinkRequest.class))).thenReturn(mockLinkResponse);

        SendMessage response = command.handle(update);

        String text = (String) response.getParameters().get("text");
        assertEquals("Ссылка добавлена в отслеживание: " + urlToTrack, text);
    }

    @Test
    void testTrackCommandLinkAlreadyTracked() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToTrack = "https://example.com";
        Long chatId = 123L;

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        when(message.text()).thenReturn("/track " + urlToTrack);
        when(scrapperApiClient.addLink(anyLong(), any(AddLinkRequest.class))).thenThrow(new LinkAlreadyAddedException("Link already tracked"));

        SendMessage response = command.handle(update);

        String text = (String) response.getParameters().get("text");
        assertEquals("Эта ссылка уже отслеживается.", text);
    }
}
