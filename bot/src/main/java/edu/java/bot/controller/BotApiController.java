package edu.java.bot.controller;

import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.bucket.BucketManager;
import edu.java.bot.dto.LinkUpdateRequest;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BotApiController {

    private final TelegramBotImpl telegramBot;
    private static final Logger LOGGER = LoggerFactory.getLogger(BotApiController.class);
    private final BucketManager bucketManager;
    private final HttpServletRequest request;

    private boolean isLimitExceeded() {
        String ip = request.getRemoteAddr();
        Bucket bucket = bucketManager.resolveBucket(ip);
        return !bucket.tryConsumeAndReturnRemaining(1).isConsumed();
    }

    @PostMapping("/updates")
    public ResponseEntity<?> postUpdate(@RequestBody LinkUpdateRequest linkUpdate) {
        if (isLimitExceeded()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
        }

        if (linkUpdate.getUrl() == null || linkUpdate.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        String messageText = String.format("%s\n\n%s", linkUpdate.getUrl(), linkUpdate.getDescription());

        linkUpdate.getTgChatIds().forEach(chatId -> {
            LOGGER.info("Sending update to chat ID {}: {}", chatId, messageText);
            telegramBot.sendChatMessage(chatId, messageText);
        });

        return ResponseEntity.noContent().build();
    }
}
