package edu.java.bot.controller;

import edu.java.bot.dto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BotApiControllerRateLimitTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Test
    public void rateLimitTest_underConcurrentLoad() throws InterruptedException {
        String baseUrl = "http://localhost:" + port + "/updates";
        LinkUpdateRequest requestPayload = new LinkUpdateRequest();
        requestPayload.setUrl("https://example.com");
        requestPayload.setDescription("Test Description");

        try (ExecutorService executorService = Executors.newFixedThreadPool(100)) {
            List<Callable<ResponseEntity<?>>> tasks = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                tasks.add(() -> restTemplate.postForEntity(baseUrl, requestPayload, String.class));
            }

            List<Future<ResponseEntity<?>>> futures = executorService.invokeAll(tasks);
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            long rateLimitExceededCount = futures.stream()
                .filter(future -> {
                    try {
                        return future.get().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
                    } catch (InterruptedException | ExecutionException e) {
                        return false;
                    }
                })
                .count();

            assertTrue(rateLimitExceededCount > 0, "Should have some requests rate limited");
        }
    }
}
