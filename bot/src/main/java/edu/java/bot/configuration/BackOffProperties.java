package edu.java.bot.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "backoff")
@Setter
@Getter
@SuppressWarnings("MagicNumber")
public class BackOffProperties {
    private String strategy = "exponential";
    private long initialDelay = 1000;
    private double multiplier = 2.0;
    private long increment = 1000;
    private int maxAttempts = 5;
    private List<Integer> retryableStatusCodes;
}
