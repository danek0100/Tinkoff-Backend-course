package edu.java.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CombinedPullRequestInfo {
    private String title;
    private List<IssuesCommentsResponse> issueComments;
    private List<PullCommentsResponse> pullComments;
}
