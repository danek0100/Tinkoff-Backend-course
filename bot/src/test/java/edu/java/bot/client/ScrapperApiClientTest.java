package edu.java.bot.client;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.java.bot.backoff.BackOffStrategy;
import edu.java.bot.backoff.ConstBackOff;
import edu.java.bot.bucket.BucketManager;
import edu.java.bot.configuration.BackOffProperties;
import edu.java.bot.dto.AddLinkRequest;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import edu.java.bot.dto.RemoveLinkRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.mock.mockito.MockBean;

class ScrapperApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @MockBean
    private BucketManager bucketManager;

    private ScrapperApiClient scrapperApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("Expected response");

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        BackOffProperties backOffProperties = new BackOffProperties();
        backOffProperties.setRetryableStatusCodes(Collections.singletonList(500));
        backOffProperties.setInitialDelay(100);
        backOffProperties.setMaxAttempts(2);
        BackOffStrategy backOffStrategy = new ConstBackOff(backOffProperties.getInitialDelay());

        scrapperApiClient = new ScrapperApiClient("http://localhost:8080", mockHttpClient,
            objectMapper, backOffStrategy, backOffProperties);
    }


    @Test
    void testRetryOnFailure() throws Exception {
        Long tgChatId = 1L;
        when(mockHttpResponse.statusCode()).thenReturn(500, 200);

        String result = scrapperApiClient.getAllLinks(tgChatId);

        assertEquals("Expected response", result);
        verify(mockHttpClient, times(2)).send(any(HttpRequest.class), any());
    }

    @Test
    void testRegisterChat() throws Exception {
        Long chatId = 1L;
        String result = scrapperApiClient.registerChat(chatId);
        assertEquals("Expected response", result);
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testDeleteChat() throws Exception {
        Long chatId = 1L;
        String result = scrapperApiClient.deleteChat(chatId);
        assertEquals("Expected response", result);
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testAddLink() throws Exception {
        AddLinkRequest addLinkRequest = new AddLinkRequest("http://example.com");
        Long tgChatId = 1L;
        String result = scrapperApiClient.addLink(tgChatId, addLinkRequest);
        assertEquals("Expected response", result);
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllLinks() throws Exception {
        Long tgChatId = 1L;
        String result = scrapperApiClient.getAllLinks(tgChatId);
        assertEquals("Expected response", result);
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testRemoveLink() throws Exception {
        Long tgChatId = 1L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://example.com");
        String result = scrapperApiClient.removeLink(tgChatId, removeLinkRequest);
        assertEquals("Expected response", result);
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }


}
