package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(UntrackCommand.class);

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Прекратить отслеживание ссылки";
    }

    @Override
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /untrack command");
        String messageText = update.message().text();
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2 || !edu.java.bot.utils.LinkParser.isValidURL(parts[1])) {
            return new SendMessage(update.message().chat().id(), "Пожалуйста, предоставьте валидный URL.");
        }

        if (LinkStorage.removeLink(update.message().chat().id(), parts[1])) {
            LOGGER.info("Removed link from tracking: {}", parts[1]);
            return new SendMessage(update.message().chat().id(), "Ссылка удалена из отслеживания: " + parts[1]);
        } else {
            return new SendMessage(update.message().chat().id(), "Эта ссылка не была найдена в списке отслеживаемых.");
        }
    }
}
