package edu.java.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.topic}")
    private String updateTopic;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(updateTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public String updateTopicName() {
        return updateTopic;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
