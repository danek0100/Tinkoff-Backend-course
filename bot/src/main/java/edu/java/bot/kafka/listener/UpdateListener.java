package edu.java.bot.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.LinkUpdateRequest;
import edu.java.bot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateListener.class);

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String update) {
        try {
            LinkUpdateRequest updateRequest = objectMapper.readValue(update, LinkUpdateRequest.class);
            notificationService.processUpdate(updateRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("Deserializing fail: " + e.getMessage());
        }
    }
}
