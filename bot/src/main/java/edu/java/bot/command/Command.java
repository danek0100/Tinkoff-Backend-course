package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Objects;

public interface Command {

    String command();

    String description();

    SendMessage handle(Update update);

    default boolean supports(Update update) {
        try {
            return Objects.equals(command(), update.message().text().split(" ")[0]);
        } catch (Exception e) {
            return false;
        }
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
