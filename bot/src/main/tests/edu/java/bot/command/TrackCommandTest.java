package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrackCommandTest {

    @BeforeEach
    void setup() {
        LinkStorage.clear();
    }

    @Test
    void testTrackCommandResponse() {
        TrackCommand command = new TrackCommand();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        String urlToTrack = "https://example.com";

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/track " + urlToTrack);

        SendMessage response = command.handle(update);
        String text = (String) response.getParameters().get("text");
        assertEquals("Ссылка добавлена в отслеживание: " + urlToTrack, text);
        assertTrue(LinkStorage.getLinks(123L).contains(urlToTrack));
    }
}
