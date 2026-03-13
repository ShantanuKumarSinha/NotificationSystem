package com.shann.notificationsystem.controller;

import com.shann.notificationsystem.model.Channel;
import com.shann.notificationsystem.model.NotificationEvent;
import com.shann.notificationsystem.producer.NotificationProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationProducer notificationProducer;

    public NotificationController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String sendNotification() {
        // For demonstration, we will create a dummy notification event
        // In a real application, you would accept a request body with the notification details
        var event = new NotificationEvent(
                UUID.randomUUID().toString(),
                "user123",
                "This is a test notification.",
                Channel.EMAIL,
                "Test Notification",
                "This is a test notification.",
                java.util.Map.of("key1", "value1", "key2", "value2"),
                java.time.Instant.now()
        );

        notificationProducer.sendNotificationEvent(event);
        return event.eventId();
    }
}
