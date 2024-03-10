package edu.java.service;

import edu.java.client.github.GitHubClient;
import edu.java.dto.CombinedPullRequestInfo;
import edu.java.dto.IssuesCommentsResponse;
import edu.java.dto.PullCommentsResponse;
import edu.java.dto.PullRequestResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;

    @Autowired
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public Mono<CombinedPullRequestInfo> getPullRequestInfo(String owner, String repo, int pullRequestId) {
        Mono<PullRequestResponse> pullRequestDetailsMono =
            gitHubClient.fetchPullRequestDetails(owner, repo, pullRequestId);
        Mono<List<IssuesCommentsResponse>> issueCommentsMono =
            gitHubClient.fetchIssueComments(owner, repo, pullRequestId).collectList();
        Mono<List<PullCommentsResponse>> pullCommentsMono =
            gitHubClient.fetchPullComments(owner, repo, pullRequestId).collectList();

        return Mono.zip(pullRequestDetailsMono, issueCommentsMono, pullCommentsMono)
            .map(tuple -> {
                PullRequestResponse pullRequestDetails = tuple.getT1();
                List<IssuesCommentsResponse> issueComments = tuple.getT2();
                List<PullCommentsResponse> pullComments = tuple.getT3();

                CombinedPullRequestInfo combinedInfo = new CombinedPullRequestInfo();
                combinedInfo.setTitle(pullRequestDetails.getTitle());
                combinedInfo.setIssueComments(issueComments);
                combinedInfo.setPullComments(pullComments);
                return combinedInfo;
            });
    }
}
