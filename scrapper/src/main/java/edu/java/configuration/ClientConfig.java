package edu.java.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ClientConfig {

    @Bean
    public WebClient gitHubWebClient(WebClient.Builder webClientBuilder,
        @Value("${github.base.url:https://api.github.com}") String gitHubBaseUrl) {
        return webClientBuilder.baseUrl(gitHubBaseUrl).build();
    }

    @Bean
    public WebClient stackOverflowWebClient(WebClient.Builder webClientBuilder,
        @Value("${stackoverflow.base.url:https://api.stackexchange.com/2.2}") String stackOverflowBaseUrl) {
        return webClientBuilder.baseUrl(stackOverflowBaseUrl).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
