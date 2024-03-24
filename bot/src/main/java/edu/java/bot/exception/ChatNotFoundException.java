package edu.java.bot.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super(message);
    }

    public static String getExceptionName() {
        return "ChatNotFoundException";
    }
}
