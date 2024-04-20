package edu.java.configuration;

import java.time.Duration;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;


@Configuration
@AllArgsConstructor
public class RetryConfig {

    private final RetryProperties retryProperties;

    @Bean
    @ConditionalOnProperty(name = "retry.strategy", havingValue = "exponential")
    public Retry exponentialRetrySpec() {
        return Retry.backoff(retryProperties.getMaxAttempts(),
                Duration.ofSeconds(retryProperties.getFirstBackoffSeconds()))
            .maxBackoff(Duration.ofSeconds(retryProperties.getMaxBackoffSeconds()))
            .jitter(retryProperties.getJitterFactor())
            .filter(retryFilter());
    }

    @Bean
    @ConditionalOnProperty(name = "retry.strategy", havingValue = "constant")
    public Retry constantRetrySpec() {
        return Retry.fixedDelay(retryProperties.getMaxAttempts(),
                Duration.ofSeconds(retryProperties.getFirstBackoffSeconds()))
            .filter(retryFilter());
    }

    @Bean
    @ConditionalOnProperty(name = "retry.strategy", havingValue = "linear")
    public Retry linearRetrySpec() {
        return Retry.from(retrySignals -> retrySignals
            .filter(retrySignal -> retrySignal.failure() instanceof WebClientResponseException
                && retryProperties.getRetryableStatusCodes().contains(
                    ((WebClientResponseException) retrySignal.failure()).getStatusCode().value()))
            .flatMap(retrySignal ->
                Flux.just(retrySignal.totalRetriesInARow())
                    .delayElements(calculateLinearBackoff(retrySignal.totalRetriesInARow()))
            )
        );
    }

    private Duration calculateLinearBackoff(long retryCount) {
        long backoff = retryProperties.getFirstBackoffSeconds() + retryProperties.getIncrementSeconds() * retryCount;
        return Duration.ofSeconds(Math.min(backoff, retryProperties.getMaxBackoffSeconds()));
    }

    @Bean
    public Predicate<Throwable> retryFilter() {
        return throwable -> throwable instanceof WebClientResponseException
            && retryProperties.getRetryableStatusCodes().contains(
                ((WebClientResponseException) throwable).getStatusCode().value());
    }
}
