package edu.java.bot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserMessageProcessorImplTest {

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    private List<Command> commandList;
    private UserMessageProcessorImpl userMessageProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandList = new ArrayList<>();
        userMessageProcessor = new UserMessageProcessorImpl(commandList);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }

    @Test
    void whenCommandIsKnown_thenProcessCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
        when(message.text()).thenReturn("/start");

        Command startCommand = mock(Command.class);
        when(startCommand.supports(any())).thenReturn(true);
        when(startCommand.command()).thenReturn("/start");

        List<Command> commandList = new ArrayList<>();
        commandList.add(startCommand);
        UserMessageProcessor messageProcessor = new UserMessageProcessorImpl(commandList);

        messageProcessor.process(update);
        verify(startCommand, times(1)).handle(update);
    }

    @Test
    void whenCommandIsUnknown_thenDoNotProcess() {
        String commandText = "/unknown";
        when(message.text()).thenReturn(commandText);

        SendMessage response = userMessageProcessor.process(update);
        String text = (String) response.getParameters().get("text");
        assertEquals("Неизвестная команда.", text);
    }
}
