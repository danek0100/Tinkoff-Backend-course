package edu.java.configuration;

import edu.java.jooq.service.JooqChatLinkService;
import edu.java.jooq.service.JooqChatService;
import edu.java.jooq.service.JooqLinkService;
import edu.java.service.ChatLinkService;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public LinkService linkService(DSLContext dslContext) {
        return new JooqLinkService(dslContext);
    }

    @Bean
    public ChatService chatService(DSLContext dslContext) {
        return new JooqChatService(dslContext);
    }

    @Bean
    public ChatLinkService chatLinkService(DSLContext dslContext) {
        return new JooqChatLinkService(dslContext);
    }
}
