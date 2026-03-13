package com.shann.notificationsystem.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationEvent(String eventId, String userId, String message, Channel channel, String title, String body, Map<String, String> metadata, Instant createdAt) {

    public NotificationEvent{
        if(eventId == null || eventId.isEmpty()) {
            eventId = UUID.randomUUID().toString();
        }
    }

}
