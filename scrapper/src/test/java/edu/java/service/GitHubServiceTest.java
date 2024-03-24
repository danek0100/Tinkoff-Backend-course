package edu.java.service;

import edu.java.client.github.GitHubClient;
import edu.java.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Flux.fromIterable;

public class GitHubServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        PullRequestResponse mockPullRequestResponse = new PullRequestResponse(1L, "Test PR", "", "");
        IssuesCommentsResponse mockIssueComment = new IssuesCommentsResponse("", 1L,
            OffsetDateTime.now(), OffsetDateTime.now(), "Issue Comment");
        PullCommentsResponse mockPullComment = new PullCommentsResponse("", 2L, "",
            OffsetDateTime.now(), OffsetDateTime.now(), "Pull Comment");

        when(gitHubClient.fetchPullRequestDetails(anyString(), anyString(), anyInt()))
            .thenReturn(Mono.just(mockPullRequestResponse));
        when(gitHubClient.fetchIssueComments(anyString(), anyString(), anyInt()))
            .thenReturn(fromIterable(Collections.singletonList(mockIssueComment)));
        when(gitHubClient.fetchPullComments(anyString(), anyString(), anyInt()))
            .thenReturn(fromIterable(Collections.singletonList(mockPullComment)));
    }

    @Test
    public void testGetPullRequestInfo() {
        Mono<CombinedPullRequestInfo> result = gitHubService.getPullRequestInfo("owner", "repo", 1);

        StepVerifier.create(result)
            .assertNext(combinedInfo -> {
                assert combinedInfo.getTitle().equals("Test PR");
                assert combinedInfo.getIssueComments().size() == 1;
                assert combinedInfo.getPullComments().size() == 1;
                assert combinedInfo.getIssueComments().get(0).getBody().equals("Issue Comment");
                assert combinedInfo.getPullComments().get(0).getBody().equals("Pull Comment");
            })
            .verifyComplete();
    }
}
