package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import java.util.List;

public interface UserMessageProcessor {
    List<Command> getCommands();

    SendMessage process(Update update);
}
