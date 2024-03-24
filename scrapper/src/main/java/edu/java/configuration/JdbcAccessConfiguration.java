package edu.java.configuration;

import edu.java.dao.ChatDao;
import edu.java.dao.ChatLinkDao;
import edu.java.dao.LinkDao;
import edu.java.jdbc.JdbcChatLinkService;
import edu.java.jdbc.JdbcChatService;
import edu.java.jdbc.JdbcLinkService;
import edu.java.service.ChatLinkService;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public LinkService linkService(LinkDao linkDao) {
        return new JdbcLinkService(linkDao);
    }

    @Bean
    public ChatService chatService(ChatDao chatDao) {
        return new JdbcChatService(chatDao);
    }

    @Bean
    public ChatLinkService chatLinkService(ChatLinkDao chatLinkDao, ChatDao chatDao) {
        return new JdbcChatLinkService(chatLinkDao, chatDao);
    }
}
