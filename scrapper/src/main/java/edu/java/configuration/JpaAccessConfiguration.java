package edu.java.configuration;

import edu.java.jpa.repository.ChatLinkRepository;
import edu.java.jpa.repository.ChatRepository;
import edu.java.jpa.repository.LinkRepository;
import edu.java.jpa.service.JpaChatLinkService;
import edu.java.jpa.service.JpaChatService;
import edu.java.jpa.service.JpaLinkService;
import edu.java.service.ChatLinkService;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public LinkService linkService(LinkRepository linkRepository) {
        return new JpaLinkService(linkRepository);
    }

    @Bean
    public ChatService chatService(ChatRepository chatRepository) {
        return new JpaChatService(chatRepository);
    }

    @Bean
    public ChatLinkService chatLinkService(ChatLinkRepository chatLinkRepository, ChatRepository chatRepository) {
        return new JpaChatLinkService(chatLinkRepository, chatRepository);
    }
}
