package edu.java.client;

import edu.java.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BotApiClient {
    private WebClient webClient;

    public BotApiClient(@Value("${bot.api.baseurl}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> postUpdate(LinkUpdateRequest linkUpdate) {
        return this.webClient.post()
                .uri("/updates")
                .bodyValue(linkUpdate)
                .retrieve()
                .bodyToMono(String.class);
    }

    void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
