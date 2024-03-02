package edu.java.bot.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import java.util.List;
import org.springframework.context.annotation.Bean;


public class CommandConfig {

    @Bean
    public HelpCommand helpCommand(List<Command> commands) {
        return new HelpCommand(commands);
    }
}
