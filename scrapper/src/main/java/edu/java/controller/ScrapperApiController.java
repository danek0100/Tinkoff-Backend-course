package edu.java.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperApiController {

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<?> registerChat(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body("Чат зарегистрирован");
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body("Чат успешно удалён");
    }

    @GetMapping("/links")
    public ResponseEntity<?> getAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        return ResponseEntity.ok().body("Ссылки успешно получены");
    }

    @PostMapping("/links")
    public ResponseEntity<?> addLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody AddLinkRequest addLinkRequest) {
        return ResponseEntity.ok().body("Ссылка успешно добавлена");
    }

    @DeleteMapping("/links")
    public ResponseEntity<?> removeLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest) {
        return ResponseEntity.ok().body("Ссылка успешно убрана");
    }
}

