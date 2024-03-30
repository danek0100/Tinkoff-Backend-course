package edu.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScrapperApiControllerRateLimitTest extends IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAddLinkUnderHighConcurrency() throws InterruptedException {
        String baseUrl = "http://localhost:" + port + "/links?tg-chat-id=1";
        AddLinkRequest requestPayload = new AddLinkRequest();
        requestPayload.setLink("https://example.com");
        requestPayload.setDescription("Example Description");

        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            List<Callable<ResponseEntity<String>>> tasks = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                tasks.add(() -> restTemplate.postForEntity(baseUrl, requestPayload, String.class));
            }

            List<Future<ResponseEntity<String>>> futures = executor.invokeAll(tasks);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            long rateLimitExceededResponses = futures.stream()
                .filter(future -> {
                    try {
                        ResponseEntity<String> response = future.get();
                        if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                            ApiErrorResponse errorResponse = new ObjectMapper().readValue(response.getBody(), ApiErrorResponse.class);
                            return "RateLimitExceededException".equals(errorResponse.getExceptionName());
                        }
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        // Обработка исключений
                    }
                    return false;
                })
                .count();

            assertTrue(rateLimitExceededResponses > 0, "Expected some requests to be rate limited");
        }

    }
}
