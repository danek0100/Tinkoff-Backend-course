package edu.java.bot.controller;

import edu.java.bot.dto.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotApiController {

    @PostMapping("/updates")
    public ResponseEntity<?> postUpdate(@RequestBody LinkUpdateRequest linkUpdate) {
        if (linkUpdate.getUrl() == null || linkUpdate.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        // Логика уведомлений

        return ResponseEntity.noContent().build();
    }
}
