package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UserMessageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelegramBotImplTest {
    @Mock
    private TelegramBot bot;
    @Mock
    private ApplicationConfig config;
    @Mock
    private UserMessageProcessor messageProcessor;
    private TelegramBotImpl telegramBotImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.telegramToken()).thenReturn("test-token");
        telegramBotImpl = new TelegramBotImpl(config, messageProcessor);
    }

    @Test
    void whenUpdatesReceived_thenProcessMessages() {
        Update update = mock(Update.class);
        List<Update> updates = List.of(update);
        SendMessage sendMessage = new SendMessage(123L, "Test Message");

        when(messageProcessor.process(update)).thenReturn(sendMessage);

        SendResponse mockSendResponse = mock(SendResponse.class);
        when(mockSendResponse.isOk()).thenReturn(true);

        when(bot.execute(any(SendMessage.class))).thenReturn(mockSendResponse);

        assertEquals(telegramBotImpl.process(updates), UpdatesListener.CONFIRMED_UPDATES_ALL);
    }

    @Test
    void whenNoUpdatesReceived_thenNothingHappens() {
        List<Update> updates = List.of();
        telegramBotImpl.process(updates);

        verify(bot, never()).execute(any());
    }
}
