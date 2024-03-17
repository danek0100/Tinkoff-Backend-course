package edu.java.service;

import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dto.AnswerResponse;
import edu.java.dto.CombinedStackOverflowInfo;
import edu.java.dto.QuestionResponse;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StackOverflowService {

    private final StackOverflowClient stackOverflowClient;

    @Autowired
    public StackOverflowService(StackOverflowClient stackOverflowClient) {
        this.stackOverflowClient = stackOverflowClient;
    }

    public Mono<QuestionResponse> getQuestionInfo(String questionId) {
        return stackOverflowClient.fetchQuestionsInfo(Collections.singletonList(questionId))
            .flatMap(list -> list.isEmpty() ? Mono.empty() : Mono.just(list.get(0)));
    }

    public Mono<List<AnswerResponse>> getAnswersForQuestion(String questionId) {
        return stackOverflowClient.fetchAnswersInfo(Collections.singletonList(questionId))
            .flatMapMany(Flux::fromIterable)
            .collectList();
    }

    public Mono<List<QuestionResponse>> getAllQuestionsInfo(List<String> questionIds) {
        return stackOverflowClient.fetchQuestionsInfo(questionIds);
    }

    public Mono<List<AnswerResponse>> getAllAnswersInfo(List<String> questionIds) {
        return stackOverflowClient.fetchAnswersInfo(questionIds);
    }

    public Mono<CombinedStackOverflowInfo> getCombinedInfo(String questionId) {
        Mono<QuestionResponse> questionMono = getQuestionInfo(questionId);
        Mono<List<AnswerResponse>> answersMono = getAnswersForQuestion(questionId);

        return Mono.zip(questionMono, answersMono, CombinedStackOverflowInfo::new);
    }
}
