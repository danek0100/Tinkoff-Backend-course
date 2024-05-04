package edu.java.bot.service;

import edu.java.bot.bot.Bot;
import edu.java.bot.dto.LinkUpdateRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final Bot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final Counter updatesProcessed = Metrics.counter("updates.processed");

    @Override
    public void processUpdate(LinkUpdateRequest update) {
        String messageText = String.format("%s\n\n%s", update.getUrl(), update.getDescription());

        update.getTgChatIds().forEach(chatId -> {
            LOGGER.info("Sending update to chat ID {}: {}", chatId, messageText);
            bot.sendChatMessage(chatId, messageText);
        });
        updatesProcessed.increment();
    }
}
