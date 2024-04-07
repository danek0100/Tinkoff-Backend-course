package edu.java.service;

import edu.java.client.BotApiClient;
import edu.java.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class HttpNotificationSenderTest {

    private BotApiClient botApiClient;
    private HttpNotificationSender httpNotificationSender;

    @BeforeEach
    void setUp() {
        botApiClient = Mockito.mock(BotApiClient.class);
        httpNotificationSender = new HttpNotificationSender(botApiClient);
    }

    @Test
    void sendNotification_Success() {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        Mockito.when(botApiClient.postUpdate(Mockito.any(LinkUpdateRequest.class)))
                .thenReturn(Mono.just("Success"));

        httpNotificationSender.sendNotification(updateRequest);

        Mockito.verify(botApiClient).postUpdate(updateRequest);
    }

    @Test
    void sendNotification_VerifyReactiveStream() {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        Mockito.when(botApiClient.postUpdate(updateRequest)).thenReturn(Mono.just("Success"));

        StepVerifier.create(botApiClient.postUpdate(updateRequest))
                .expectNext("Success")
                .verifyComplete();
    }
}
