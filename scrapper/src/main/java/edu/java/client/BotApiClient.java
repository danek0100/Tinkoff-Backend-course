package edu.java.client;

import edu.java.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

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
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .maxBackoff(Duration.ofMinutes(1))
                    .jitter(0.5)
                );
    }

    void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
