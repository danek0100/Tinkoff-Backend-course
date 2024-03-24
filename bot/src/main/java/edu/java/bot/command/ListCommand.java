package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.ListLinksResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ListCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListCommand.class);
    private final ScrapperApiClient scrapperApiClient;

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
        try {
            Long chatId = update.message().chat().id();
            ListLinksResponse response = scrapperApiClient.getAllLinks(chatId);

            if (response.getLinks().isEmpty()) {
                return new SendMessage(chatId, "Список отслеживаемых ссылок пуст.");
            } else {
                StringBuilder messageBuilder = new StringBuilder("Отслеживаемые ссылки:\n");
                for (LinkResponse link : response.getLinks()) {
                    messageBuilder.append(link.getUrl()).append("\n").append(link.getDescription()).append("\n\n");
                }
                return new SendMessage(chatId, messageBuilder.toString());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении списка отслеживаемых ссылок", e);
            return new SendMessage(update.message().chat().id(),
                "Произошла ошибка при получении списка отслеживаемых ссылок.");
        }
    }
}
