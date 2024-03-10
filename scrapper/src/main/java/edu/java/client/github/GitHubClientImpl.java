package edu.java.client.github;

import edu.java.dto.IssuesCommentsResponse;
import edu.java.dto.PullCommentsResponse;
import edu.java.dto.PullRequestResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GitHubClientImpl implements GitHubClient {

    private final WebClient webClient;

    public GitHubClientImpl(@Qualifier("gitHubWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<PullRequestResponse> fetchPullRequestDetails(String owner, String repo, int pullRequestId) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}/pulls/{pullRequestId}", owner, repo, pullRequestId)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> Mono.error(new RuntimeException("API Error")))
            .bodyToMono(PullRequestResponse.class);
    }

    @Override
    public Flux<IssuesCommentsResponse> fetchIssueComments(String owner, String repo, int issueNumber) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}/issues/{issueNumber}/comments", owner, repo, issueNumber)
            .retrieve()
            .bodyToFlux(IssuesCommentsResponse.class);
    }

    @Override
    public Flux<PullCommentsResponse> fetchPullComments(String owner, String repo, int pullNumber) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}/pulls/{issueNumber}/comments", owner, repo, pullNumber)
            .retrieve()
            .bodyToFlux(PullCommentsResponse.class);
    }
}
