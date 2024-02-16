package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Зарегистрироваться";
    }

    @Override
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /start command");
        // Adding id to db
        return new SendMessage(update.message().chat().id(), "Добро пожаловать!");
    }
}
