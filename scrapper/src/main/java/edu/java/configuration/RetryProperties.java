package edu.java.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "retry")
@Setter
@Getter
public class RetryProperties {

    private String strategy;
    private long maxAttempts;
    private long firstBackoffSeconds;
    private long maxBackoffSeconds;
    private double jitterFactor;
    private List<Integer> retryableStatusCodes;
}
