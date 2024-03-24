package edu.java.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StackOverflowLinkExtractorTests {

    @Test
    public void testExtractQuestionId() {
        String url = "https://stackoverflow.com/questions/12345678/how-to-test-code";
        Assertions.assertEquals("12345678", StackOverflowLinkExtractor.extractQuestionId(url));
    }

    @Test
    public void testInvalidStackOverflowUrl() {
        String url = "https://stackoverflow.com/not/questions";
        Assertions.assertThrows(IllegalArgumentException.class, () -> StackOverflowLinkExtractor.extractQuestionId(url));
    }
}
