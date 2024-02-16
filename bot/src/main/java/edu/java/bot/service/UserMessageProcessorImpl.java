package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMessageProcessorImpl implements UserMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageProcessorImpl.class);
    private final List<Command> commandList;

    public UserMessageProcessorImpl(List<Command> commandList) {
        this.commandList = commandList;
    }

    @Override
    public List<? extends Command> commands() {
        return commandList;
    }

    @Override
    public SendMessage process(Update update) {
        for (Command command : commandList) {
            if (command.supports(update)) {
                LOGGER.info("Processing command: {}", command.command());
                return command.handle(update);
            }
        }
        LOGGER.info("No command supports the received update. Ignoring.");
        return new SendMessage(update.message().chat().id(), "Неизвестная команда.");
    }
}
