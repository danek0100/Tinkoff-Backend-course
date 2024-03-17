package edu.java.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;


@Getter
public class CombinedStackOverflowInfo {

    private final QuestionResponse question;
    private final List<AnswerResponse> answers;
    private final OffsetDateTime latestUpdate;

    public CombinedStackOverflowInfo(QuestionResponse question, List<AnswerResponse> answers) {
        this.question = question;
        this.answers = answers;
        this.latestUpdate = calculateLatestUpdate(question, answers);
    }

    private OffsetDateTime calculateLatestUpdate(QuestionResponse question, List<AnswerResponse> answers) {
        OffsetDateTime latestDate = question.getLastActivityDate();
        for (AnswerResponse answer : answers) {
            if (answer.getCreationDate().isAfter(latestDate)) {
                latestDate = answer.getLastActivityDate();
            }
        }
        return latestDate;
    }
}
