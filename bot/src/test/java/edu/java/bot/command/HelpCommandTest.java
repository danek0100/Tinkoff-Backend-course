package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpCommandTest {

    @Test
    void testHelpCommandResponse() {
        Command startCommandMock = mock(Command.class);
        when(startCommandMock.command()).thenReturn("/start");
        when(startCommandMock.description()).thenReturn("Запустить бота");

        List<Command> commands = List.of(startCommandMock);

        HelpCommand command = new HelpCommand(commands);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        SendMessage response = command.handle(update);
        String text = (String) response.getParameters().get("text");

        StringBuilder expectedResponse = new StringBuilder("Доступные команды:\n");
        for (Command cmd : commands) {
            expectedResponse.append(cmd.command()).append(": ").append(cmd.description()).append("\n");
        }

        expectedResponse.append("\nПоддерживаются ссылки в следующем формате:\n")
            .append("GitHub: https://github.com/danek0100/Tinkoff-Backend-course/pull/5\n")
            .append("StackOverflow: https://stackoverflow.com/questions/858572/how-to-make-a-new-list-in-java");


        assertEquals(expectedResponse.toString().trim(), text.trim());
    }
}
