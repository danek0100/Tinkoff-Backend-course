package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class UserMessageProcessorImpl implements UserMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageProcessorImpl.class);
    private final Map<String, Command> commandMap = new HashMap<>();

    public UserMessageProcessorImpl(List<Command> commandList) {
        for (Command command : commandList) {
            commandMap.put(command.command(), command);
        }
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<>(commandMap.values());
    }

    @Override
    public SendMessage process(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String commandText = update.message().text().split(" ")[0];
            Command command = commandMap.get(commandText);

            if (command != null) {
                LOGGER.info("Processing command: {}", command.command());
                return command.handle(update);
            }
        }

        LOGGER.info("No command supports the received update. Ignoring.");
        return new SendMessage(update.message().chat().id(), "Неизвестная команда.");
    }
}
