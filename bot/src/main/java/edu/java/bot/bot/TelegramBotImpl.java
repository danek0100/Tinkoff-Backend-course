package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.command.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UserMessageProcessor;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotImpl implements Bot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotImpl.class);
    private final TelegramBot bot;
    private final UserMessageProcessor messageProcessor;

    public TelegramBotImpl(ApplicationConfig config, UserMessageProcessor messageProcessor) {
        this.bot = new TelegramBot(config.telegramToken());
        this.messageProcessor = messageProcessor;
    }

    @PostConstruct
    public void setupBotCommands() {
        List<Command> commands = messageProcessor.getCommands();

        BotCommand[] botCommands = commands.stream()
            .map(command -> new BotCommand(command.command(), command.description()))
            .toArray(BotCommand[]::new);

        bot.execute(new SetMyCommands(botCommands));
    }

    @PostConstruct
    @Override
    public void start() {
        LOGGER.info("Starting Telegram bot...");
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        LOGGER.info("Received {} updates", updates.size());
        for (Update update : updates) {
            LOGGER.debug("Processing update: {}", update.updateId());
            if (update.message() != null) {
                SendMessage response = messageProcessor.process(update);
                if (response != null) {
                    execute(response);
                    LOGGER.debug("Sent response to chat ID {}", response.getParameters().get("chat_id"));
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void close() {
        LOGGER.info("Shutting down Telegram bot...");
        bot.removeGetUpdatesListener();
        bot.shutdown();
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        LOGGER.debug("Executing request: {}", request.getMethod());
        bot.execute(request);
    }

    public void sendChatMessage(Long chatId, String message) {
        SendMessage request = new SendMessage(chatId.toString(), message);
        this.execute(request);
    }
}
