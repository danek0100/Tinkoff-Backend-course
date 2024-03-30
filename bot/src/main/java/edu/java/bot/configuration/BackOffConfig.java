package edu.java.bot.configuration;

import edu.java.bot.backoff.BackOffStrategy;
import edu.java.bot.backoff.ConstBackOff;
import edu.java.bot.backoff.ExponentialBackOff;
import edu.java.bot.backoff.LinearBackOff;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BackOffProperties.class)
public class BackOffConfig {

    private final BackOffProperties backOffProperties;

    public BackOffConfig(BackOffProperties backOffProperties) {
        this.backOffProperties = backOffProperties;
    }

    @Bean
    public BackOffStrategy backOffStrategy() {
        return switch (backOffProperties.getStrategy()) {
            case "constant" -> new ConstBackOff(backOffProperties.getInitialDelay());
            case "linear" -> new LinearBackOff(backOffProperties.getInitialDelay(), backOffProperties.getIncrement());
            default -> new ExponentialBackOff(backOffProperties.getInitialDelay(), backOffProperties.getMultiplier());
        };
    }
}
