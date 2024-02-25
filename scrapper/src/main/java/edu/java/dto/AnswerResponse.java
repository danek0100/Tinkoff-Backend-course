package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerResponse {
    @JsonProperty("answer_id")
    private Long answerId;

    @JsonProperty("creation_date")
    private OffsetDateTime creationDate;

    @JsonProperty("question_id")
    private Long questionId;
}
