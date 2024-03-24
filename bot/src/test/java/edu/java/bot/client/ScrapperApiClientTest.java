package edu.java.bot.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.ListLinksResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.ObjectMapper;


class ScrapperApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ScrapperApiClient scrapperApiClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockHttpResponse.body()).thenReturn("Expected response");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);
        scrapperApiClient = new ScrapperApiClient("http://localhost:8080", mockHttpClient, objectMapper);
    }

    @Test
    void testRegisterChat() throws Exception {
        Long chatId = 1L;
        when(mockHttpResponse.statusCode()).thenReturn(204);

        scrapperApiClient.registerChat(chatId);

        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testDeleteChat() throws Exception {
        Long chatId = 1L;
        when(mockHttpResponse.statusCode()).thenReturn(204);

        scrapperApiClient.deleteChat(chatId);

        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testAddLink() throws Exception {
        Long tgChatId = 1L;
        AddLinkRequest addLinkRequest = new AddLinkRequest("http://example.com", "Description");
        LinkResponse expectedResponse = new LinkResponse(1L, "http://example.com", "Description");
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);

        LinkResponse result = scrapperApiClient.addLink(tgChatId, addLinkRequest);

        assertEquals(expectedResponse.getUrl(), result.getUrl());
        assertEquals(expectedResponse.getDescription(), result.getDescription());
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllLinks() throws Exception {
        Long tgChatId = 1L;
        List<LinkResponse> links = Collections.singletonList(new LinkResponse(1L, "http://example.com", "Description"));
        ListLinksResponse expectedResponse = new ListLinksResponse(links, links.size());
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);

        ListLinksResponse result = scrapperApiClient.getAllLinks(tgChatId);

        assertEquals(expectedResponse.getSize(), result.getSize());
        assertEquals(expectedResponse.getLinks().size(), result.getLinks().size());
        assertEquals(expectedResponse.getLinks().get(0).getUrl(), result.getLinks().get(0).getUrl());
        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testRemoveLink() throws Exception {
        Long tgChatId = 1L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("http://example.com");
        LinkResponse expectedLinkResponse = new LinkResponse(1L, "http://example.com", "Example description");
        String jsonResponse = objectMapper.writeValueAsString(expectedLinkResponse);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);

        LinkResponse actualLinkResponse = scrapperApiClient.removeLink(tgChatId, removeLinkRequest);

        assertEquals(expectedLinkResponse.getId(), actualLinkResponse.getId());
        assertEquals(expectedLinkResponse.getUrl(), actualLinkResponse.getUrl());
        assertEquals(expectedLinkResponse.getDescription(), actualLinkResponse.getDescription());

        verify(mockHttpClient).send(any(HttpRequest.class), any());
    }

}
