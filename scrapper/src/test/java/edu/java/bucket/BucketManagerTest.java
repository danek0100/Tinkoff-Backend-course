package edu.java.bucket;

import edu.java.configuration.RateLimitingProperties;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BucketManagerTest {

    private BucketManager bucketManager;
    @Mock
    private RateLimitingProperties properties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(properties.getCapacity()).thenReturn(20);
        when(properties.getTokens()).thenReturn(20);
        when(properties.getRefillDuration()).thenReturn(60);
        bucketManager = new BucketManager(properties);
    }

    @Test
    void resolveBucket_createsNewBucket_ifNotExists() {
        String ip = "127.0.0.1";
        Bucket bucket1 = bucketManager.resolveBucket(ip);
        assertNotNull(bucket1);

        Bucket bucket2 = bucketManager.resolveBucket(ip);
        assertSame(bucket1, bucket2);
    }
}
