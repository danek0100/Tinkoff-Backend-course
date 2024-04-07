package edu.java.bot.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.LinkUpdateRequest;
import edu.java.bot.service.NotificationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UpdateListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateListener.class);
    private final String deadLetterQueueTopicName;
    private final Validator validator;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String update) {
        try {
            LinkUpdateRequest updateRequest = objectMapper.readValue(update, LinkUpdateRequest.class);
            Set<ConstraintViolation<LinkUpdateRequest>> violations = validator.validate(updateRequest);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            notificationService.processUpdate(updateRequest);
        } catch (Exception e) {
            LOGGER.error("Processing fail: " + e.getMessage());
            sendToDlq(update);
        }
    }

    private void sendToDlq(String failedMessage) {
        LOGGER.info("Sending to DLQ: {}", failedMessage);
        kafkaTemplate.send(deadLetterQueueTopicName, failedMessage);
    }
}
