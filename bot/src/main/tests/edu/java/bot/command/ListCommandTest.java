package edu.java.bot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListCommandTest {

    @BeforeEach
    void setup() {
        LinkStorage.clear();
    }

    @Test
    void testListCommandWithEmptyList() {
        ListCommand command = new ListCommand();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        SendMessage response = command.handle(update);
        try {
            String text = (String) response.getParameters().get("text");
            assertEquals("Список отслеживаемых ссылок пуст.", text);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void testListCommandWithNonEmptyList() {
        LinkStorage.addLink(123L, "https://example.com");
        ListCommand command = new ListCommand();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        SendMessage response = command.handle(update);
        String text = (String) response.getParameters().get("text");
        assertTrue(text.contains("https://example.com"));
    }
}
