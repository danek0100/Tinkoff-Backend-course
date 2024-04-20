package edu.java.bot.backoff;

public class ConstBackOff implements BackOffStrategy {
    private final long backOffPeriod;

    public ConstBackOff(long backOffPeriod) {
        this.backOffPeriod = backOffPeriod;
    }

    @Override
    public void backOff() throws InterruptedException {
        Thread.sleep(backOffPeriod);
    }

    @Override
    public void reset() {

    }
}
