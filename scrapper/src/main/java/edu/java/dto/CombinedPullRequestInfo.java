package edu.java.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CombinedPullRequestInfo {
    private String title;
    private List<IssuesCommentsResponse> issueComments;
    private List<PullCommentsResponse> pullComments;
}
