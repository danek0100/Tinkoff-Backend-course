package edu.java.bucket;

import edu.java.configuration.RateLimitingProperties;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class BucketManager {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitingProperties properties;

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::createNewBucket);
    }

    private Bucket createNewBucket(String ip) {
        Refill refill = Refill.greedy(properties.getTokens(), Duration.ofSeconds(properties.getRefillDuration()));
        Bandwidth limit = Bandwidth.classic(properties.getCapacity(), refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
