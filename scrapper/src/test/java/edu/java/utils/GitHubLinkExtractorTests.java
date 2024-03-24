package edu.java.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GitHubLinkExtractorTests {

    @Test
    public void testExtractOwner() {
        String url = "https://github.com/danek0100/Tinkoff-Backend-course/pull/6";
        Assertions.assertEquals("danek0100", GitHubLinkExtractor.extractOwner(url));
    }

    @Test
    public void testExtractRepo() {
        String url = "https://github.com/danek0100/Tinkoff-Backend-course/pull/6";
        Assertions.assertEquals("Tinkoff-Backend-course", GitHubLinkExtractor.extractRepo(url));
    }

    @Test
    public void testExtractPullRequestId() {
        String url = "https://github.com/danek0100/Tinkoff-Backend-course/pull/6";
        Assertions.assertEquals(6, GitHubLinkExtractor.extractPullRequestId(url));
    }

    @Test
    public void testInvalidGitHubUrl() {
        String url1 = "https://github.com/";
        Assertions.assertThrows(IllegalArgumentException.class, () -> GitHubLinkExtractor.extractOwner(url1));
        String url2 = "https://github.com/danek0100";
        Assertions.assertThrows(IllegalArgumentException.class, () -> GitHubLinkExtractor.extractRepo(url2));
        String url3 = "https://github.com/danek0100/Tinkoff-Backend-course/pull/";
        Assertions.assertThrows(IllegalArgumentException.class, () -> GitHubLinkExtractor.extractPullRequestId(url3));
    }
}
