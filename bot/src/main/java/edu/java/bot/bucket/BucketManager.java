package edu.java.bot.bucket;

import edu.java.bot.configuration.RateLimitingProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
