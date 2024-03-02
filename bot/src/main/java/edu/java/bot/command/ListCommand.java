package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListCommand.class);

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Показать список отслеживаемых ссылок";
    }

    @Override
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /list command");
        Set<String> links = LinkStorage.getLinks(update.message().chat().id());
        if (links.isEmpty()) {
            return new SendMessage(update.message().chat().id(), "Список отслеживаемых ссылок пуст.");
        } else {
            StringBuilder messageBuilder = new StringBuilder("Отслеживаемые ссылки:\n");
            links.forEach(link -> messageBuilder.append(link).append("\n"));
            return new SendMessage(update.message().chat().id(), messageBuilder.toString());
        }
    }
}
