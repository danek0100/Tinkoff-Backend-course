package edu.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String updateTopic;

   public void send(LinkUpdateRequest update) throws JsonProcessingException {
       kafkaTemplate.send(updateTopic, objectMapper.writeValueAsString(update));
   }
}
