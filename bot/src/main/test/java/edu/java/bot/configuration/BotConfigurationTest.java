package edu.java.bot.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.service.UserMessageProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {BotConfiguration.class})
public class BotConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertNotNull(context);
    }

    @Test
    public void userMessageProcessorBeanExists() {
        UserMessageProcessor processor = context.getBean(UserMessageProcessor.class);
        assertNotNull(processor);
    }

    @Test
    public void commandBeansExist() {
        assertTrue(context.containsBeanDefinition("commands"));
        List<Command> commands = context.getBean("commands", List.class);
        assertFalse(commands.isEmpty());
        assertTrue(commands.size() >= 5);
        assertTrue(commands.get(0) instanceof StartCommand);
        assertTrue(commands.get(1) instanceof HelpCommand);
        assertTrue(commands.get(2) instanceof ListCommand);
        assertTrue(commands.get(3) instanceof TrackCommand);
        assertTrue(commands.get(4) instanceof UntrackCommand);
    }
}
