package edu.java.client.stackoverflow;

import edu.java.dto.AnswerResponse;
import edu.java.dto.AnswersApiResponse;
import edu.java.dto.QuestionResponse;
import edu.java.dto.QuestionsApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StackOverflowClientImpl implements StackOverflowClient {

    private static final String SITE = "site";
    private static final String STACKOVERFLOW = "stackoverflow";
    private static final String API_ERROR = "API Error";

    private final WebClient webClient;

    public StackOverflowClientImpl(@Qualifier("stackOverflowWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<QuestionResponse>> fetchQuestionsInfo(List<String> questionIds) {
        String joinedQuestionIds = String.join(";", questionIds);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{ids}")
                .queryParam(SITE, STACKOVERFLOW)
                .build(joinedQuestionIds))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new RuntimeException(API_ERROR)))
            .bodyToMono(QuestionsApiResponse.class)
            .map(QuestionsApiResponse::getItems);
    }



    @Override
    public Mono<List<AnswerResponse>> fetchAnswersInfo(List<String> questionIds) {
        String joinedQuestionIds = String.join(";", questionIds);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam(SITE, STACKOVERFLOW)
                .build(joinedQuestionIds))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new RuntimeException(API_ERROR)))
            .bodyToMono(AnswersApiResponse.class)
            .map(AnswersApiResponse::getItems);
    }
}
