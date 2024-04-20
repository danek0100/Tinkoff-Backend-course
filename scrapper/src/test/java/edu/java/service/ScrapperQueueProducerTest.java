package edu.java.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScrapperQueueProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private ScrapperQueueProducer scrapperQueueProducer;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scrapperQueueProducer = new ScrapperQueueProducer(kafkaTemplate, objectMapper, "update-topic");
    }

    @Test
    void send_ShouldSerializeAndUpdateCorrectly() throws Exception {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setUrl("example.com");
        updateRequest.setDescription("desc");
        updateRequest.setTgChatIds(Collections.singletonList(123L));

        String expectedSerializedMessage = "{\"id\":1,\"url\":\"example.com\",\"description\":\"desc\",\"tgChatIds\":[123]}";

        when(objectMapper.writeValueAsString(updateRequest)).thenReturn(expectedSerializedMessage);

        scrapperQueueProducer.send(updateRequest);

        verify(objectMapper).writeValueAsString(eq(updateRequest));
        verify(kafkaTemplate).send(eq("update-topic"), messageCaptor.capture());

        String capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage).isEqualTo(expectedSerializedMessage);
    }
}
