package edu.java.configuration;

import edu.java.client.BotApiClient;
import edu.java.service.HttpNotificationSender;
import edu.java.service.KafkaNotificationSender;
import edu.java.service.NotificationSender;
import edu.java.service.ScrapperQueueProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public NotificationSender notificationSender(ApplicationConfig applicationConfig,
        ScrapperQueueProducer scrapperQueueProducer, BotApiClient botApiClient) {
        if (applicationConfig.useQueue()) {
            return new KafkaNotificationSender(scrapperQueueProducer);
        } else {
            return new HttpNotificationSender(botApiClient);
        }
    }
}
