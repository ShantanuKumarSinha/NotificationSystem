package com.shann.notificationsystem.model;

import java.util.Map;

public record NotificationRequest(
        String userId,
        Channel channel,
        String title,
        String body,
        Map<String, String> metadata) {

}

