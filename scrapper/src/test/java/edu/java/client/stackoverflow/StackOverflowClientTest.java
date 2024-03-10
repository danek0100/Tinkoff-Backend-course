package edu.java.client.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.dto.AnswerResponse;
import edu.java.dto.QuestionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"stackoverflow.base.url=http://localhost:8089"})
public class StackOverflowClientTest {
    private WireMockServer wireMockServer;

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089); // Порт, на котором будет запущен WireMock
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        setUpQuestionsInfo();
        setUpAnswersInfo();
    }

    void setUpQuestionsInfo() {
        wireMockServer.stubFor(get(urlPathEqualTo("/questions/123%3B456"))
            .withQueryParam("site", equalTo("stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"items\": [{\"question_id\": 123, \"title\": \"Test Question 1\"}, {\"question_id\": 456, \"title\": \"Test Question 2\"}]}")));
    }

    void setUpAnswersInfo() {
        wireMockServer.stubFor(get(urlPathEqualTo("/questions/123/answers"))
            .withQueryParam("site", equalTo("stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"items\": [{\"answer_id\": 789, \"question_id\": 123}]}")));
    }

    @Test
    void fetchQuestionsInfoTest() {
        List<String> questionIds = Arrays.asList("123", "456");
        Mono<List<QuestionResponse>> response = stackOverflowClient.fetchQuestionsInfo(questionIds);
        StepVerifier.create(response)
            .expectNextMatches(questions -> questions.size() == 2 && questions.get(0).getTitle().equals("Test Question 1"))
            .verifyComplete();
    }

    @Test
    void fetchAnswersInfoTest() {
        List<String> questionIds = Collections.singletonList("123");
        Mono<List<AnswerResponse>> response = stackOverflowClient.fetchAnswersInfo(questionIds);
        StepVerifier.create(response)
            .expectNextMatches(answers -> answers.size() == 1 && answers.get(0).getAnswerId() == 789 && answers.get(0).getQuestionId() == 123)
            .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
}
