package edu.java.scheduler;

import edu.java.client.BotApiClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.dto.CombinedPullRequestInfo;
import edu.java.dto.LinkDTO;
import edu.java.dto.LinkUpdateRequest;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.service.ChatLinkService;
import edu.java.service.GitHubService;
import edu.java.service.LinkService;
import edu.java.service.StackOverflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import static org.mockito.Mockito.*;

public class LinkUpdaterSchedulerTest {

    @Mock
    private LinkService linkService;
    @Mock
    private ChatLinkService chatLinkService;
    @Mock
    private GitHubService gitHubService;
    @Mock
    private StackOverflowService stackOverflowService;
    @Mock
    private BotApiClient botApiClient;
    @Mock
    private ApplicationConfig applicationConfig;
    @Mock
    private ApplicationConfig.Scheduler schedulerConfig;

    private LinkUpdaterScheduler scheduler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(applicationConfig.scheduler()).thenReturn(schedulerConfig);
        when(schedulerConfig.enable()).thenReturn(true);
        when(schedulerConfig.interval()).thenReturn(Duration.ofDays(1L));

        scheduler = new LinkUpdaterScheduler(applicationConfig, linkService, chatLinkService, gitHubService, stackOverflowService, botApiClient);
    }

    @Test
    public void updateTest() {
        LinkDTO mockLink = new LinkDTO(1L, "https://stackoverflow.com/", "test", LocalDateTime.now(),
            LocalDateTime.now(), LocalDateTime.now());
        when(linkService.findLinksToCheck(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockLink));
        when(gitHubService.getPullRequestInfo(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(new CombinedPullRequestInfo("Title", new ArrayList<>(), new ArrayList<>())));
        when(botApiClient.postUpdate(any(LinkUpdateRequest.class)))
                .thenReturn(Mono.just("Success"));

        scheduler.update();

        verify(linkService).findLinksToCheck(any(LocalDateTime.class));
    }
}
