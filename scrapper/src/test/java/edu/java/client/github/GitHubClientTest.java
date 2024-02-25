package edu.java.client.github;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import edu.java.dto.IssuesCommentsResponse;
import edu.java.dto.PullCommentsResponse;
import edu.java.dto.PullRequestResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"github.base.url=http://localhost:8089"})
public class GitHubClientTest {

    private WireMockServer wireMockServer;

    @Autowired
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089); // Порт, на котором будет запущен WireMock
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        setUpPullRequestInfo();
        setUpIssueComments();
        setUpPullComments();
    }

    void setUpPullRequestInfo() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/owner/repo/pulls/1"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"id\": 1, \"title\": \"Test PR\", \"review_comments_url\": \"\", \"comments_url\": \"\"}")));
    }

    void setUpIssueComments() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/owner/repo/issues/1/comments"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("[{\"id\": 2, \"url\": \"http://example.com\", \"created_at\": \"2020-01-01T00:00:00Z\", \"body\": \"Test comment\"}]")));
    }

    void setUpPullComments() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/owner/repo/pulls/1/comments"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("[{\"id\": 3, \"url\": \"http://example.com\", \"created_at\": \"2020-01-02T00:00:00Z\", \"body\": \"Test pull comment\"}]")));
    }

    @Test
    void fetchPullRequestDetailsTest() {
        Mono<PullRequestResponse> response = gitHubClient.fetchPullRequestDetails("owner", "repo", 1);
        StepVerifier.create(response)
            .expectNextMatches(pr -> pr.getId() == 1 && pr.getTitle().equals("Test PR"))
            .verifyComplete();
    }

    @Test
    void fetchIssueCommentsTest() {
        Flux<IssuesCommentsResponse> response = gitHubClient.fetchIssueComments("owner", "repo", 1);
        StepVerifier.create(response)
            .expectNextMatches(comment -> comment.getId() == 2 && comment.getBody().equals("Test comment"))
            .verifyComplete();
    }

    @Test
    void fetchPullCommentsTest() {
        Flux<PullCommentsResponse> response = gitHubClient.fetchPullComments("owner", "repo", 1);
        StepVerifier.create(response)
            .expectNextMatches(comment -> comment.getId() == 3 && comment.getBody().equals("Test pull comment"))
            .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
}
