package edu.java.bot.controller;

import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.bucket.BucketManager;
import edu.java.bot.dto.LinkUpdateRequest;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BotApiController.class)
public class BotApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TelegramBotImpl telegramBot;

    @MockBean
    private BucketManager bucketManager;

    @Mock
    private Bucket mockBucket;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        ConsumptionProbe probe = Mockito.mock(ConsumptionProbe.class);
        when(probe.isConsumed()).thenReturn(true);
        when(mockBucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

        when(bucketManager.resolveBucket(anyString())).thenReturn(mockBucket);

    }

    @Test
    public void testPostUpdate() throws Exception {
        LinkUpdateRequest request = new LinkUpdateRequest();
        request.setUrl("http://example.com");
        request.setDescription("Пример описания");
        request.setTgChatIds(Arrays.asList(123L, 456L));

        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        String expectedMessage = "http://example.com\n\nПример описания";
        verify(telegramBot, times(1)).sendChatMessage(123L, expectedMessage);
        verify(telegramBot, times(1)).sendChatMessage(456L, expectedMessage);
    }
}
