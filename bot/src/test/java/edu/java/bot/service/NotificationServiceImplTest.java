package edu.java.bot.service;

import edu.java.bot.bot.Bot;
import edu.java.bot.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class NotificationServiceImplTest {

    @Mock
    private Bot bot;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Captor
    private ArgumentCaptor<Long> chatIdCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processUpdate_sendsMessagesToAllChatIds() {
        LinkUpdateRequest updateRequest = new LinkUpdateRequest();
        updateRequest.setUrl("http://example.com");
        updateRequest.setDescription("Test description");
        updateRequest.setTgChatIds(Arrays.asList(123L, 456L));

        notificationService.processUpdate(updateRequest);

        verify(bot, times(updateRequest.getTgChatIds().size())).sendChatMessage(chatIdCaptor.capture(), messageCaptor.capture());

        assertTrue(chatIdCaptor.getAllValues().containsAll(updateRequest.getTgChatIds()));

        String expectedMessageText = "http://example.com\n\nTest description";
        messageCaptor.getAllValues().forEach(message ->
                assertThat(message).isEqualTo(expectedMessageText));
    }
}
