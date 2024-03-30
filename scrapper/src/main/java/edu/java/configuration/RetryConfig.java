package edu.java.configuration;

import java.time.Duration;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;


@Configuration
@AllArgsConstructor
public class RetryConfig {

    private final RetryProperties retryProperties;

    @Bean
    public Retry retrySpec() {
        return Retry.backoff(retryProperties.getMaxAttempts(),
                Duration.ofSeconds(retryProperties.getFirstBackoffSeconds()))
            .maxBackoff(Duration.ofSeconds(retryProperties.getMaxBackoffSeconds()))
            .filter(retryFilter());
    }

    @Bean
    public Predicate<Throwable> retryFilter() {
        return throwable -> throwable instanceof WebClientResponseException
            && retryProperties.getRetryableStatusCodes().contains(
                ((WebClientResponseException) throwable).getStatusCode().value());
    }
}
