package edu.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationSender implements NotificationSender {

    private final ScrapperQueueProducer scrapperQueueProducer;

    @Override
    public void sendNotification(LinkUpdateRequest update) {
        try {
            scrapperQueueProducer.send(update);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message", e);
        }
    }
}
