package edu.java.bot.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.LinkUpdateRequest;
import edu.java.bot.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"update-topic", "topic_dlq"},
               brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class UpdateListenerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldCallNotificationServiceWhenMessageReceived() throws JsonProcessingException, InterruptedException {
        // Создаём реальный инстанс, чтобы отправлять сообщения
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        KafkaTemplate<String, String> realKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()));

        ObjectMapper objectMapper = new ObjectMapper();
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        updateRequest.setId(10L);
        updateRequest.setUrl("example.com");
        String jsonUpdate = objectMapper.writeValueAsString(updateRequest);

        Thread.sleep(2000);

        realKafkaTemplate.send("update-topic", jsonUpdate);
        realKafkaTemplate.flush();

        Thread.sleep(2000);

        verify(notificationService, times(1)).processUpdate(any(LinkUpdateRequest.class));
    }

    @Test
    void shouldSendToDlqWhenProcessingFails() throws InterruptedException {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        KafkaTemplate<String, String> realKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()));

        String invalidMessage = "{\"error\": \"this is message with errors\"}";

        Thread.sleep(2000);

        realKafkaTemplate.send("update-topic", invalidMessage);
        realKafkaTemplate.flush();

        Thread.sleep(2000);

        verify(kafkaTemplate, times(1)).send(eq("topic_dlq"), eq(invalidMessage));
    }
}

