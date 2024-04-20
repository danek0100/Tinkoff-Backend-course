package edu.java.bot.backoff;

public class LinearBackOff implements BackOffStrategy {
    private final long initialDelay;
    private final long increment;
    private long currentDelay;

    public LinearBackOff(long initialDelay, long increment) {
        this.initialDelay = initialDelay;
        this.increment = increment;
        this.currentDelay = initialDelay;
    }

    @Override
    public void backOff() throws InterruptedException {
        Thread.sleep(currentDelay);
        currentDelay += increment;
    }

    @Override
    public void reset() {
        this.currentDelay = initialDelay;
    }
}
