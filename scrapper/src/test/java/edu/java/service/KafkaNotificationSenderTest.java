package edu.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.java.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KafkaNotificationSenderTest {

    private ScrapperQueueProducer scrapperQueueProducer;
    private KafkaNotificationSender kafkaNotificationSender;

    @BeforeEach
    void setUp() {
        scrapperQueueProducer = Mockito.mock(ScrapperQueueProducer.class);
        kafkaNotificationSender = new KafkaNotificationSender(scrapperQueueProducer);
    }

    @Test
    void sendNotification_CallsScrapperQueueProducerSend() throws JsonProcessingException {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setUrl("example.com");

        kafkaNotificationSender.sendNotification(updateRequest);
        Mockito.verify(scrapperQueueProducer).send(updateRequest);
    }

    @Test
    void sendNotification_ThrowsRuntimeExceptionOnJsonProcessingException() throws JsonProcessingException {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        updateRequest.setId(2L);
        updateRequest.setUrl("failed-example.com");

        Mockito.doThrow(JsonProcessingException.class).when(scrapperQueueProducer).send(updateRequest);
        assertThrows(RuntimeException.class, () -> kafkaNotificationSender.sendNotification(updateRequest));
    }
}
