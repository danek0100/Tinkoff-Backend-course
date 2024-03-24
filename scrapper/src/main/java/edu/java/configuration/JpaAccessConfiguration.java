package edu.java.configuration;

import edu.java.jpa.ChatLinkRepository;
import edu.java.jpa.ChatRepository;
import edu.java.jpa.JpaChatLinkService;
import edu.java.jpa.JpaChatService;
import edu.java.jpa.JpaLinkService;
import edu.java.jpa.LinkRepository;
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
