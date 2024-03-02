package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class HelpCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);
    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Список доступных команд";
    }

    @Override
    public SendMessage handle(Update update) {
        StringBuilder messageText = new StringBuilder("Доступные команды:\n");
        for (Command command : commands) {
            messageText.append(command.command()).append(": ").append(command.description()).append("\n");
        }
        LOGGER.info("Handling /help command");
        return new SendMessage(update.message().chat().id(), messageText.toString());
    }
}
