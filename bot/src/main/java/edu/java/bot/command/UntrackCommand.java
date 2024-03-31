package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import edu.java.bot.exception.ChatNotFoundException;
import edu.java.bot.exception.LinkNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UntrackCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(UntrackCommand.class);
    private final ScrapperApiClient scrapperApiClient;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Прекратить отслеживание ссылки";
    }

    @Override
    @SuppressWarnings("ReturnCount")
    public SendMessage handle(Update update) {
        LOGGER.info("Handling /untrack command");
        String messageText = update.message().text();
        String[] parts = messageText.split(" ", 2);
        if (parts.length < 2 || !edu.java.bot.utils.LinkParser.isValidURL(parts[1])) {
            return new SendMessage(update.message().chat().id(), "Пожалуйста, предоставьте валидный URL.");
        }

        try {
            Long chatId = update.message().chat().id();
            LinkResponse link = scrapperApiClient.removeLink(chatId, new RemoveLinkRequest(parts[1]));
            LOGGER.info("Removed link from tracking: {}", link.getUrl());
            return new SendMessage(chatId, "Ссылка удалена из отслеживания: " + link.getUrl());
        } catch (LinkNotFoundException e) {
            LOGGER.error("Link already deleted: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(), "Эта ссылка не отслеживается.");
        } catch (ChatNotFoundException e) {
            LOGGER.error("Chat not found for adding: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(), "Необходима регистрация! Выполните /start.");
        } catch (Exception e) {
            LOGGER.error("Error removing link from tracking: {}", parts[1], e);
            return new SendMessage(update.message().chat().id(),
                "Произошла ошибка при удалении ссылки. Возможно, она не найдена.");
        }
    }
}
