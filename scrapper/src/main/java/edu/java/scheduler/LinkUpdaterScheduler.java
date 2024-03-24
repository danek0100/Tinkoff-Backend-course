package edu.java.scheduler;

import edu.java.client.BotApiClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.dto.ChatLinkDTO;
import edu.java.dto.Comment;
import edu.java.dto.LinkDTO;
import edu.java.dto.LinkUpdateRequest;
import edu.java.service.ChatLinkService;
import edu.java.service.GitHubService;
import edu.java.service.LinkService;
import edu.java.service.StackOverflowService;
import edu.java.utils.GitHubLinkExtractor;
import edu.java.utils.StackOverflowLinkExtractor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class LinkUpdaterScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdaterScheduler.class);
    private static final int CHECK_LINK_AFTER = 10;
    private final ApplicationConfig.Scheduler schedulerConfig;
    private final LinkService linkService;
    private final ChatLinkService chatLinkService;
    private final GitHubService gitHubService;
    private final StackOverflowService stackOverflowService;
    private final BotApiClient botApiClient;

    @Autowired
    public LinkUpdaterScheduler(ApplicationConfig applicationConfig, LinkService linkService,
            ChatLinkService chatLinkService, GitHubService gitHubService, StackOverflowService stackOverflowService,
            BotApiClient botApiClient) {
        this.schedulerConfig = applicationConfig.scheduler();
        this.linkService = linkService;
        this.chatLinkService = chatLinkService;
        this.gitHubService = gitHubService;
        this.stackOverflowService = stackOverflowService;
        this.botApiClient = botApiClient;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        Collection<LinkDTO> outdatedLinks = linkService
            .findLinksToCheck(LocalDateTime.now().minusMinutes(CHECK_LINK_AFTER));

        Flux.fromIterable(outdatedLinks)
            .flatMap(this::checkAndUpdateLink)
            .subscribe(
                updatedLink -> LOGGER.info("Link updated: {}", updatedLink.getUrl()),
                error -> LOGGER.error("Error updating link", error),
                () -> LOGGER.info("Update process completed.")
            );
    }

    private Mono<LinkDTO> checkAndUpdateLink(LinkDTO link) {
        if (link.getUrl().contains("github.com") && link.getUrl().contains("pull")) {
            return checkGitHubLink(link);
        } else if (link.getUrl().contains("stackoverflow.com")) {
            return checkStackOverflowLink(link);
        }
        return Mono.just(link);
    }

    private Mono<LinkDTO> checkGitHubLink(LinkDTO link) {
        String owner = GitHubLinkExtractor.extractOwner(link.getUrl());
        String repo = GitHubLinkExtractor.extractRepo(link.getUrl());
        int pullRequestId = GitHubLinkExtractor.extractPullRequestId(link.getUrl());

        gitHubService.getPullRequestInfo(owner, repo, pullRequestId).subscribe(combinedInfo -> {
            OffsetDateTime latestCommentDate = Stream.concat(
                    combinedInfo.getIssueComments().stream(),
                    combinedInfo.getPullComments().stream())
                .map(Comment::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

            if (latestCommentDate != null && (link.getLastUpdateTime() == null
                || latestCommentDate.isAfter(
                    link.getLastUpdateTime().atZone(ZoneId.systemDefault()).toInstant().atOffset(ZoneOffset.UTC))
            )) {

                LinkUpdateRequest updateRequest = new LinkUpdateRequest(
                    link.getLinkId(),
                    link.getUrl(),
                    link.getDescription(),
                    chatLinkService.findAllChatsForLink(link.getLinkId())
                        .stream()
                        .map(ChatLinkDTO::getChatId)
                        .collect(Collectors.toList())
                );

                botApiClient.postUpdate(updateRequest).subscribe();
                updateLinkWithNewTimes(link, LocalDateTime.now(), latestCommentDate.toLocalDateTime());
            } else {
                updateLinkLastCheckTime(link, LocalDateTime.now());
            }
        });
        return Mono.just(link);
    }

    private Mono<LinkDTO> checkStackOverflowLink(LinkDTO link) {
        String questionId = StackOverflowLinkExtractor.extractQuestionId(link.getUrl());

        return stackOverflowService.getCombinedInfo(questionId)
            .flatMap(combinedInfo -> {
                if (link.getLastUpdateTime() == null
                    || combinedInfo.getLatestUpdate().isAfter(
                        link.getLastUpdateTime().atZone(ZoneId.systemDefault()).toInstant().atOffset(ZoneOffset.UTC)
                )) {
                    LinkUpdateRequest updateRequest = new LinkUpdateRequest(
                        link.getLinkId(),
                        link.getUrl(),
                        link.getDescription(),
                        chatLinkService.findAllChatsForLink(link.getLinkId())
                            .stream()
                            .map(ChatLinkDTO::getChatId)
                            .collect(Collectors.toList())
                    );

                    return botApiClient.postUpdate(updateRequest)
                        .thenReturn(updateLinkWithNewTimes(link, LocalDateTime.now(),
                            combinedInfo.getLatestUpdate().toLocalDateTime()));
                } else {
                    return Mono.just(updateLinkLastCheckTime(link, LocalDateTime.now()));
                }
            });
    }

    private LinkDTO updateLinkWithNewTimes(LinkDTO link, LocalDateTime checkTime, LocalDateTime updateTime) {
        link.setLastCheckTime(checkTime);
        link.setLastUpdateTime(updateTime);
        linkService.update(link);
        return link;
    }

    private LinkDTO updateLinkLastCheckTime(LinkDTO link, LocalDateTime checkTime) {
        link.setLastCheckTime(checkTime);
        linkService.update(link);
        return link;
    }

}
