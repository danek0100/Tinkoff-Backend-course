package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.exception.ChatNotFoundException;
import edu.java.bot.exception.LinkAlreadyAddedException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TrackCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackCommand.class);
    private final ScrapperApiClient scrapperApiClient;

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Начать отслеживание ссылки";
    }

    @Override
    @SuppressWarnings({"ReturnCount", "MagicNumber"})
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /track command");
        String messageText = update.message().text();
        String[] parts = messageText.split(" ", 3);
        if (parts.length < 2 || !edu.java.bot.utils.LinkParser.isValidURL(parts[1])) {
            return new SendMessage(update.message().chat().id(), "Пожалуйста, предоставьте валидный URL.");
        }

        String description = "";
        if (parts.length > 2) {
            description = parts[2];
        }

        try {
            Long chatId = update.message().chat().id();
            LinkResponse linkResponse = scrapperApiClient.addLink(chatId, new AddLinkRequest(parts[1], description));
            LOGGER.info("Added link to tracking: {}", linkResponse.getUrl());
            return new SendMessage(chatId, "Ссылка добавлена в отслеживание: " + linkResponse.getUrl());
        } catch (LinkAlreadyAddedException e) {
            LOGGER.error("Link already tracked: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(), "Эта ссылка уже отслеживается.");
        } catch (ChatNotFoundException e) {
            LOGGER.error("Chat not found for adding: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(), "Необходима регистрация! Выполните /start.");
        } catch (Exception e) {
            LOGGER.error("Failed to add link to tracking: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(), "Произошла ошибка при добавлении ссылки.");
        }
    }
}
