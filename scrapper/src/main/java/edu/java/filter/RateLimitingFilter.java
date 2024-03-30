package edu.java.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bucket.BucketManager;
import edu.java.dto.ApiErrorResponse;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Order(1)
@AllArgsConstructor
public class RateLimitingFilter implements Filter {

    private final BucketManager bucketManager;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String ip = request.getRemoteAddr();
        Bucket bucket = bucketManager.resolveBucket(ip);

        if (bucket.tryConsumeAndReturnRemaining(1).isConsumed()) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .description("Too many requests")
                .code(HttpStatus.TOO_MANY_REQUESTS.toString())
                .exceptionName("RateLimitExceededException")
                .exceptionMessage("You have exceeded the number of allowed requests. Please try again later.")
                .stacktrace(Collections.emptyList())
                .build();
            response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
        }
    }
}
