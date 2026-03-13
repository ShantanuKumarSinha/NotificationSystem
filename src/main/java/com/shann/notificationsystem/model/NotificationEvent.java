package com.shann.notificationsystem.model;

import java.time.Instant;
import java.util.Map;

public record NotificationEvent(String eventId, String userId, String message, Channel channel, String title, String body, Map<String, String> metadata, Instant createdAt) {
}
