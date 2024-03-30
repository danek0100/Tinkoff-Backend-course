package edu.java.client;

import edu.java.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BotApiClient {
    private WebClient webClient;
    private final Retry retrySpec;

    public BotApiClient(@Value("${bot.api.baseurl}") String baseUrl, Retry retrySpec) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.retrySpec = retrySpec;
    }

    public Mono<String> postUpdate(LinkUpdateRequest linkUpdate) {
        return this.webClient.post()
                .uri("/updates")
                .bodyValue(linkUpdate)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(retrySpec);
    }

    void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
