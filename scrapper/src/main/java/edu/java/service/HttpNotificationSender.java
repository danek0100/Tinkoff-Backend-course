package edu.java.service;

import edu.java.client.BotApiClient;
import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpNotificationSender implements NotificationSender {

    private final BotApiClient botApiClient;

    @Override
    public void sendNotification(LinkUpdateRequest update) {
        botApiClient.postUpdate(update).subscribe();
    }
}
