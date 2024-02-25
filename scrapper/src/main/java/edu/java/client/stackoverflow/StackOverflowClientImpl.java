package edu.java.client.stackoverflow;

import edu.java.dto.AnswerResponse;
import edu.java.dto.AnswersApiResponse;
import edu.java.dto.QuestionResponse;
import edu.java.dto.QuestionsApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class StackOverflowClientImpl implements StackOverflowClient {

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
                .queryParam("site", "stackoverflow")
                .build(joinedQuestionIds))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new RuntimeException("API Error")))
            .bodyToMono(QuestionsApiResponse.class)
            .map(QuestionsApiResponse::getItems);
    }



    @Override
    public Mono<List<AnswerResponse>> fetchAnswersInfo(List<String> questionIds) {
        String joinedQuestionIds = String.join(";", questionIds);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam("site", "stackoverflow")
                .build(joinedQuestionIds))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new RuntimeException("API Error")))
            .bodyToMono(AnswersApiResponse.class)
            .map(AnswersApiResponse::getItems);
    }
}
