package edu.java.utils;

@SuppressWarnings("MagicNumber")
public class GitHubLinkExtractor {

    private GitHubLinkExtractor() {}

    private static final String INVALID_GITHUB_URL  = "Invalid GitHub URL: ";

    public static String extractOwner(String url) {
        String[] parts = url.split("/");
        // https://github.com/danek0100/Tinkoff-Backend-course/pull/6
        // 0          1     2       3        4                       5    6
        // Проверяем, достаточно ли частей в URL для извлечения owner.
        if (parts.length > 3) {
            return parts[3];
        }
        throw new IllegalArgumentException(INVALID_GITHUB_URL + url);
    }

    public static String extractRepo(String url) {
        String[] parts = url.split("/");
        if (parts.length > 4) {
            return parts[4];
        }
        throw new IllegalArgumentException(INVALID_GITHUB_URL + url);
    }

    public static int extractPullRequestId(String url) {
        String[] parts = url.split("/");
        if (parts.length > 6 && "pull".equals(parts[5])) {
            try {
                return Integer.parseInt(parts[6]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Pull request ID is not a valid number in URL: " + url);
            }
        }
        throw new IllegalArgumentException(INVALID_GITHUB_URL + url);
    }
}
