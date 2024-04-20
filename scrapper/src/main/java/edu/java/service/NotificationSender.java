package edu.java.service;

import edu.java.dto.LinkUpdateRequest;

public interface NotificationSender {
    void sendNotification(LinkUpdateRequest update);
}
