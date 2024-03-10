package edu.java.service;

import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dto.AnswerResponse;
import edu.java.dto.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class StackOverflowServiceTest {

    @Mock
    private StackOverflowClient stackOverflowClient;

    @InjectMocks
    private StackOverflowService stackOverflowService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        QuestionResponse mockQuestionResponse = new QuestionResponse(1L, "Test Question");
        AnswerResponse mockAnswerResponse = new AnswerResponse(1L, OffsetDateTime.now(), 1L);

        when(stackOverflowClient.fetchQuestionsInfo(anyList()))
            .thenReturn(Mono.just(Collections.singletonList(mockQuestionResponse)));
        when(stackOverflowClient.fetchAnswersInfo(anyList()))
            .thenReturn(Mono.just(Collections.singletonList(mockAnswerResponse)));
    }

    @Test
    public void getQuestionInfoTest() {
        Mono<QuestionResponse> result = stackOverflowService.getQuestionInfo("1");

        StepVerifier.create(result)
            .expectNextMatches(question -> question.getQuestionId() == 1L && question.getTitle().equals("Test Question"))
            .verifyComplete();
    }

    @Test
    public void getAnswersForQuestionTest() {
        Mono<List<AnswerResponse>> result = stackOverflowService.getAnswersForQuestion("1");

        StepVerifier.create(result)
            .expectNextMatches(answers -> answers.size() == 1 && answers.get(0).getAnswerId() == 1L)
            .verifyComplete();
    }

    @Test
    public void getAllQuestionsInfoTest() {
        Mono<List<QuestionResponse>> result = stackOverflowService.getAllQuestionsInfo(Arrays.asList("1", "4"));

        StepVerifier.create(result)
            .expectNextMatches(questions -> questions.size() == 1 && questions.get(0).getTitle().equals("Test Question"))
            .verifyComplete();
    }

    @Test
    public void getAllAnswersInfoTest() {
        Mono<List<AnswerResponse>> result = stackOverflowService.getAllAnswersInfo(Arrays.asList("1", "4"));

        StepVerifier.create(result)
            .expectNextMatches(answers -> answers.size() == 1 && answers.get(0).getAnswerId() == 1L)
            .verifyComplete();
    }
}
