package edu.java.bot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.RemoveLinkRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScrapperApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final static String TG_CHAT_URI = "/tg-chat/";
    private final static String LINKS_URI = "/links";
    private final static String TG_CHAT_ID = "Tg-Chat-Id";

    public ScrapperApiClient(@Value("${scrapper.api.baseurl}") String baseUrl,
        HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");
    }

    public String registerChat(Long id) throws Exception {
        HttpRequest request = requestBuilder(TG_CHAT_URI + id)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return sendRequest(request);
    }

    public String deleteChat(Long id) throws Exception {
        HttpRequest request = requestBuilder(TG_CHAT_URI + id)
                .DELETE()
                .build();

        return sendRequest(request);
    }

    public String getAllLinks(Long tgChatId) throws Exception {
        HttpRequest request = requestBuilder(LINKS_URI)
                .header(TG_CHAT_ID, tgChatId.toString())
                .GET()
                .build();

        return sendRequest(request);
    }

    public String addLink(Long tgChatId, AddLinkRequest addLinkRequest) throws Exception {
        String requestBody = objectMapper.writeValueAsString(addLinkRequest);

        HttpRequest request = requestBuilder(LINKS_URI)
                .header(TG_CHAT_ID, tgChatId.toString())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request);
    }

    public String removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) throws Exception {
        String requestBody = objectMapper.writeValueAsString(removeLinkRequest);

        HttpRequest request = requestBuilder(LINKS_URI)
                .header(TG_CHAT_ID, tgChatId.toString())
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request);
    }
}
