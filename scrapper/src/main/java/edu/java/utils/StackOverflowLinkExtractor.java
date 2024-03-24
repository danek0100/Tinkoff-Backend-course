package edu.java.utils;

@SuppressWarnings("MagicNumber")
public class StackOverflowLinkExtractor {

    private StackOverflowLinkExtractor() {}

    public static String extractQuestionId(String url) {
        String[] parts = url.split("/");
        if (parts.length > 4 && "questions".equals(parts[3])) {
            return parts[4];
        }
        throw new IllegalArgumentException("Invalid StackOverflow URL: " + url);
    }
}
