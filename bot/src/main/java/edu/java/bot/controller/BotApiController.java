package edu.java.bot.controller;

import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.dto.LinkUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotImpl.class);

    @PostMapping("/updates")
    public ResponseEntity<?> postUpdate(@RequestBody LinkUpdateRequest linkUpdate) {
        if (linkUpdate.getUrl() == null || linkUpdate.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        for (Long chatId : linkUpdate.getTgChatIds()) {
            LOGGER.info(chatId + " " + linkUpdate.getUrl());
        }
        // Логика уведомлений

        return ResponseEntity.noContent().build();
    }
}
