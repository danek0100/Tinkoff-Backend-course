package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperApiClient;
import edu.java.bot.exception.ChatAlreadyRegisteredException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StartCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);
    private final ScrapperApiClient scrapperApiClient;

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
        Long chatId = update.message().chat().id();
        try {
            scrapperApiClient.registerChat(chatId);
            LOGGER.info("/start: чат {} успешно зарегистрирован.", chatId);
            return new SendMessage(chatId, "Добро пожаловать!");
        } catch (ChatAlreadyRegisteredException e) {
            LOGGER.info("/start: чат {} уже зарегистрирован.", chatId);
            return new SendMessage(chatId, "Чат уже зарегистрирован.");
        } catch (Exception e) {
            LOGGER.error("/start: ошибка при регистрации чата {}.", chatId, e);
            return new SendMessage(chatId, "Ошибка при регистрации. Попробуйте позже.");
        }
    }
}
