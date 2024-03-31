package edu.java.bot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.backoff.BackOffStrategy;
import edu.java.bot.configuration.BackOffProperties;
import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.ApiErrorResponse;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.ListLinksResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import edu.java.bot.exception.ChatAlreadyRegisteredException;
import edu.java.bot.exception.ChatNotFoundException;
import edu.java.bot.exception.LinkAlreadyAddedException;
import edu.java.bot.exception.LinkNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ScrapperApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final static String TG_CHAT_URI = "/tg-chat/";
    private final static String LINKS_URI = "/links";
    private final static String TG_CHAT_ID = "Tg-Chat-Id";
    private final BackOffStrategy backOffStrategy;
    private final BackOffProperties backOffProperties;
    private final static String API_ERROR = "API error: ";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapperApiClient.class);

    public ScrapperApiClient(@Value("${scrapper.api.baseurl}") String baseUrl,
        HttpClient httpClient, ObjectMapper objectMapper, BackOffStrategy backOffStrategy,
        BackOffProperties backOffProperties) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.backOffStrategy = backOffStrategy;
        this.backOffProperties = backOffProperties;
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        backOffStrategy.reset();
        int attempt = 0;
        HttpResponse<String> response;

        do {
            if (attempt > 0) {
                backOffStrategy.backOff();
            }

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (!backOffProperties.getRetryableStatusCodes().contains(response.statusCode())
                || attempt >= backOffProperties.getMaxAttempts()) {
                break;
            }

            attempt++;
        } while (true);

        if (response.statusCode() >= HttpStatus.OK.value()
            && response.statusCode() < HttpStatus.MULTIPLE_CHOICES.value()) {
            return response;
        } else {
            throw new RuntimeException("Failed to send request after " + attempt
                + " attempts, status code: " + response.statusCode());
        }
    }
  
    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");
    }

    public void registerChat(Long id) throws Exception {
        HttpRequest request = requestBuilder(TG_CHAT_URI + id)
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() >= HttpStatus.BAD_REQUEST.value()) {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
            if (ChatAlreadyRegisteredException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new ChatAlreadyRegisteredException(errorResponse.getExceptionMessage());
            }
            throw new Exception(API_ERROR + errorResponse.getExceptionMessage());
        } else if (response.statusCode() != HttpStatus.NO_CONTENT.value()) {
            throw new Exception("Ошибка при регистрации чата.");
        }
    }

    public void deleteChat(Long id) throws Exception {
        HttpRequest request = requestBuilder(TG_CHAT_URI + id)
                .DELETE()
                .build();
      
        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() >= HttpStatus.BAD_REQUEST.value()) {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
            if (ChatNotFoundException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new ChatNotFoundException(errorResponse.getExceptionMessage());
            }
            throw new Exception(API_ERROR + errorResponse.getExceptionMessage());
        } else if (response.statusCode() != HttpStatus.NO_CONTENT.value()) {
            throw new Exception("Ошибка при удалении чата.");
        }
    }

    public ListLinksResponse getAllLinks(Long tgChatId) throws Exception {
        HttpRequest request = requestBuilder(LINKS_URI)
            .header(TG_CHAT_ID, tgChatId.toString())
            .GET()
            .build();

        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() == HttpStatus.OK.value()) {
            return objectMapper.readValue(response.body(), ListLinksResponse.class);
        } else {
            throw new Exception("Ошибка при получении списка ссылок");
        }
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest) throws Exception {
        String requestBody = objectMapper.writeValueAsString(addLinkRequest);
        HttpRequest request = requestBuilder(LINKS_URI)
            .header(TG_CHAT_ID, tgChatId.toString())
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();


        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() >= HttpStatus.BAD_REQUEST.value()) {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
            if (LinkAlreadyAddedException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new LinkAlreadyAddedException(errorResponse.getExceptionMessage());
            } else if (ChatNotFoundException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new ChatNotFoundException(errorResponse.getExceptionMessage());
            }
            throw new Exception(API_ERROR + errorResponse.getExceptionMessage());
        }

        return objectMapper.readValue(response.body(), LinkResponse.class);
    }

    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) throws Exception {
        String requestBody = objectMapper.writeValueAsString(removeLinkRequest);
        HttpRequest request = requestBuilder(LINKS_URI)
            .header(TG_CHAT_ID, tgChatId.toString())
            .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() >= HttpStatus.BAD_REQUEST.value()) {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
            if (LinkNotFoundException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new LinkNotFoundException(errorResponse.getExceptionMessage());
            } else if (ChatNotFoundException.getExceptionName().equals(errorResponse.getExceptionName())) {
                throw new ChatNotFoundException(errorResponse.getExceptionMessage());
            }
            throw new Exception(API_ERROR + errorResponse.getExceptionMessage());
        }
        return objectMapper.readValue(response.body(), LinkResponse.class);
    }
}
