package edu.java.bot.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.service.UserMessageProcessor;
import edu.java.bot.service.UserMessageProcessorImpl;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfiguration {

    @Bean
    public UserMessageProcessor userMessageProcessor(List<Command> commands) {
        return new UserMessageProcessorImpl(commands);
    }

    @Bean
    public List<Command> commands() {
        return List.of(
            new StartCommand(),
            new HelpCommand(),
            new ListCommand(),
            new TrackCommand(),
            new UntrackCommand()
        );
    }
}
