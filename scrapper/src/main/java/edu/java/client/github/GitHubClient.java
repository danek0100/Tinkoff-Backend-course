package edu.java.client.github;

import edu.java.dto.IssuesCommentsResponse;
import edu.java.dto.PullCommentsResponse;
import edu.java.dto.PullRequestResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GitHubClient {
    Mono<PullRequestResponse> fetchPullRequestDetails(String owner, String repo, int pullRequestId);

    Flux<IssuesCommentsResponse> fetchIssueComments(String owner, String repo, int issueNumber);

    Flux<PullCommentsResponse> fetchPullComments(String owner, String repo, int pullNumber);
}
