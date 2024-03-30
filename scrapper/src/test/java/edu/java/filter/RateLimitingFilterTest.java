package edu.java.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bucket.BucketManager;
import edu.java.dto.ApiErrorResponse;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimitingFilterTest {

    @Mock
    private BucketManager bucketManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private Bucket bucket;

    private RateLimitingFilter filter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new RateLimitingFilter(bucketManager, objectMapper);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(bucketManager.resolveBucket(anyString())).thenReturn(bucket);
    }

    @Test
    public void doFilter_allowsRequest_whenBucketIsNotExhausted() throws Exception {
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(ConsumptionProbe.consumed(1L, 1L));

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void doFilter_blocksRequest_whenBucketIsExhausted() throws Exception {
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(ConsumptionProbe.rejected(0L, 0L, 0L));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        filter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response).setContentType("application/json");

        String result = sw.toString();
        ApiErrorResponse responseObj = objectMapper.readValue(result, ApiErrorResponse.class);

        assertTrue(result.contains("Too many requests"));
        assertEquals("Too many requests", responseObj.getDescription());
        assertEquals(responseObj.getCode(), HttpStatus.TOO_MANY_REQUESTS.toString());
    }
}
