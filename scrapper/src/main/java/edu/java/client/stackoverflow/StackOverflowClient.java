package edu.java.client.stackoverflow;

import edu.java.dto.AnswerResponse;
import edu.java.dto.QuestionResponse;
import java.util.List;
import reactor.core.publisher.Mono;


public interface StackOverflowClient {
    Mono<List<QuestionResponse>> fetchQuestionsInfo(List<String> questionIds);

    Mono<List<AnswerResponse>> fetchAnswersInfo(List<String> questionIds);
}
