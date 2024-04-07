package edu.java.bot.service;

import edu.java.bot.dto.LinkUpdateRequest;

public interface NotificationService {
    void processUpdate(LinkUpdateRequest update);
}
