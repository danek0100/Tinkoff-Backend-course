package edu.java.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Setter
@Getter
@ToString
public class CombinedPullRequestInfo {
    private String title;
    private List<IssuesCommentsResponse> issueComments;
    private List<PullCommentsResponse> pullComments;
}
