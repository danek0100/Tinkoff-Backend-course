package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackCommand.class);

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Начать отслеживание ссылки";
    }

    @Override
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /track command");
        String messageText = update.message().text();
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2 || !edu.java.bot.utils.LinkParser.isValidURL(parts[1])) {
            return new SendMessage(update.message().chat().id(), "Пожалуйста, предоставьте валидный URL.");
        }

        if (LinkStorage.addLink(update.message().chat().id(), parts[1])) {
            LOGGER.info("Added link to tracking: {}", parts[1]);
            return new SendMessage(update.message().chat().id(), "Ссылка добавлена в отслеживание: " + parts[1]);
        } else {
            return new SendMessage(update.message().chat().id(), "Эта ссылка уже отслеживается.");
        }
    }
}
