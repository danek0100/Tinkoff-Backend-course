package edu.java.client;

import edu.java.client.BotApiClient;
import edu.java.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Import(BotApiClient.class)
public class BotApiClientTest {

    @Autowired
    private BotApiClient botApiClient;

    private WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
    private WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        WebClient webClientMock = Mockito.mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClientMock.post().uri(anyString()).body(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("Mock response"));

        this.botApiClient.setWebClient(webClientMock);
    }


    @Test
    public void testPostUpdate() {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest();

        Mono<String> resultMono = botApiClient.postUpdate(linkUpdateRequest);

        resultMono.subscribe(result -> {
            assert result.equals("Mock response");
        });
    }
}
