package edu.java.bot.backoff;

public class ExponentialBackOff implements BackOffStrategy {
    private final long initialDelay;
    private final double multiplier;
    private long currentDelay;

    public ExponentialBackOff(long initialDelay, double multiplier) {
        this.initialDelay = initialDelay;
        this.multiplier = multiplier;
        this.currentDelay = initialDelay;
    }

    @Override
    public void backOff() throws InterruptedException {
        Thread.sleep(currentDelay);
        currentDelay = Math.round(currentDelay * multiplier);
    }

    @Override
    public void reset() {
        this.currentDelay = initialDelay;
    }
}
