package edu.java.bot.backoff;

public interface BackOffStrategy {

    void backOff() throws InterruptedException;

    void reset();
}
