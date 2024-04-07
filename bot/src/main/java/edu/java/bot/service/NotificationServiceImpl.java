package edu.java.bot.service;

import edu.java.bot.bot.Bot;
import edu.java.bot.dto.LinkUpdateRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final Bot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void processUpdate(LinkUpdateRequest update) {
        String messageText = String.format("%s\n\n%s", update.getUrl(), update.getDescription());

        update.getTgChatIds().forEach(chatId -> {
            LOGGER.info("Sending update to chat ID {}: {}", chatId, messageText);
            bot.sendChatMessage(chatId, messageText);
        });
    }
}
