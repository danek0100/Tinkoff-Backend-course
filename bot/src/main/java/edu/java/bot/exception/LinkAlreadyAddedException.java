package edu.java.bot.exception;

public class LinkAlreadyAddedException extends RuntimeException {
    public LinkAlreadyAddedException(String message) {
        super(message);
    }

    public static String getExceptionName() {
        return "LinkAlreadyAddedException";
    }
}
