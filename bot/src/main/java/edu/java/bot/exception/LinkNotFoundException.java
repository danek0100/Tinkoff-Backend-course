package edu.java.bot.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String message) {
        super(message);
    }

    public static String getExceptionName() {
        return "LinkNotFoundException";
    }
}
