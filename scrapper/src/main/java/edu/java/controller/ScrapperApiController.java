package edu.java.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ChatLinkDTO;
import edu.java.dto.LinkDTO;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.service.ChatLinkService;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ScrapperApiController {

    private final ChatService chatService;
    private final LinkService linkService;
    private final ChatLinkService chatLinkService;

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<?> registerChat(@PathVariable("id") Long id) {
        chatService.register(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable("id") Long id) {
        chatService.unregister(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        Collection<ChatLinkDTO> chatLinks = chatLinkService.findAllLinksForChat(tgChatId);
        List<LinkResponse> links = chatLinks.stream()
            .map(chatLink -> {
                LinkDTO link = linkService.findById(chatLink.getLinkId());
                return new LinkResponse(link.getLinkId(), link.getUrl(), link.getDescription());
            })
            .collect(Collectors.toList());

        ListLinksResponse response = new ListLinksResponse(links, links.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/links")
    public ResponseEntity<?> addLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody AddLinkRequest addLinkRequest) {
        LinkDTO addedLink = linkService.add(addLinkRequest.getLink(), addLinkRequest.getDescription());

        chatLinkService.addLinkToChat(tgChatId, addedLink.getLinkId());

        LinkResponse response =
            new LinkResponse(addedLink.getLinkId(), addedLink.getUrl(), addedLink.getDescription());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/links")
    public ResponseEntity<?> removeLink(@RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest) {
        Long linkId = linkService.findByUrl(removeLinkRequest.getLink()).getLinkId();

        chatLinkService.removeLinkFromChat(tgChatId, linkId);

        if (!chatLinkService.existsChatsForLink(linkId)) {
            linkService.remove(removeLinkRequest.getLink());
        }

        return ResponseEntity.noContent().build();
    }

}

