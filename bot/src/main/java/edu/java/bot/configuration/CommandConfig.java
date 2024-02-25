package edu.java.bot.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;


public class CommandConfig {

    @Bean
    public HelpCommand helpCommand(List<Command> commands) {
        List<Command> filteredCommands = commands.stream()
            .filter(command -> !(command instanceof HelpCommand))
            .collect(Collectors.toList());
        return new HelpCommand(filteredCommands);
    }
}
