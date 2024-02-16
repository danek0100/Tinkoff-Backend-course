package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

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
        LOGGER.info("Handling /help command");
        return new SendMessage(update.message().chat().id(),
            "Доступные команды: /start, /help, /track, /untrack, /list");
    }
}
