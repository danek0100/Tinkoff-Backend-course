package edu.java.bot.configuration;

import edu.java.bot.backoff.BackOffStrategy;
import edu.java.bot.backoff.ConstBackOff;
import edu.java.bot.backoff.ExponentialBackOff;
import edu.java.bot.backoff.LinearBackOff;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BackOffProperties.class)
public class BackOffConfig {

    private final BackOffProperties backOffProperties;

    @Bean
    @ConditionalOnProperty(name = "backoff.strategy", havingValue = "constant")
    public BackOffStrategy constBackOffStrategy() {
        return new ConstBackOff(backOffProperties.getInitialDelay());
    }

    @Bean
    @ConditionalOnProperty(name = "backoff.strategy", havingValue = "linear")
    public BackOffStrategy linearBackOffStrategy() {
        return new LinearBackOff(backOffProperties.getInitialDelay(), backOffProperties.getIncrement());
    }

    @Bean
    @ConditionalOnProperty(name = "backoff.strategy", havingValue = "exponential")
    public BackOffStrategy exponentialBackOffStrategy() {
        return new ExponentialBackOff(backOffProperties.getInitialDelay(), backOffProperties.getMultiplier());
    }
}
