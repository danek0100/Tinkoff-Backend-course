package edu.java.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
@Getter
@Setter
public class RateLimitingProperties {

    private int capacity;
    private int tokens;
    private int refillDuration;
}
