package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import edu.java.bot.configuration.ApplicationConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TelegramBotService {

    private final TelegramBot bot;
    private final ApplicationConfig config;
    private int offset = 0;

    @Autowired
    public TelegramBotService(ApplicationConfig config) {
        this.config = config;
        this.bot = new TelegramBot(config.telegramToken());
    }

    @PostConstruct
    public void setupBotCommands() {
        bot.execute(new SetMyCommands(
            new BotCommand("/start", "Зарегистрироваться"),
            new BotCommand("/help", "Список доступных команд"),
            new BotCommand("/track", "Начать отслеживание ссылки"),
            new BotCommand("/untrack", "Прекратить отслеживание ссылки"),
            new BotCommand("/list", "Показать список отслеживаемых ссылок")
        ));
    }

    @Scheduled(fixedDelay = 3000)
    public void fetchUpdates() {
        GetUpdates getUpdatesRequest = new GetUpdates().limit(100).offset(offset).timeout(0);
        GetUpdatesResponse updatesResponse = bot.execute(getUpdatesRequest);
        List<Update> updates = updatesResponse.updates();

        for (Update update : updates) {
            processUpdate(update);
            offset = update.updateId() + 1;
        }
    }

    public void processUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String text = update.message().text();
            long chatId = update.message().chat().id();

            System.out.println(text + " " + chatId);
            switch (text) {
                case "/start":
                    // TODO Handle start command
                    sendMessage(chatId, "Welcome! You are now registered.");
                    break;
                case "/help":
                    // TODO Handle help command
                    sendMessage(chatId, "Commands: /start, /help, /track, /untrack, /list");
                    break;
                // TODO Implement /track, /untrack, and /list cases
            }
            sendMessage(chatId, text);
        }
    }

    public void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
