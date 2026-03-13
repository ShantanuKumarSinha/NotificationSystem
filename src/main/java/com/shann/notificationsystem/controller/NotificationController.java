package com.shann.notificationsystem.controller;

import com.shann.notificationsystem.model.NotificationEvent;
import com.shann.notificationsystem.producer.NotificationProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationProducer notificationProducer;

    public NotificationController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String sendNotification(@RequestBody NotificationEvent notificationEvent) {
        // Generate a unique event ID for tracking
        notificationProducer.sendNotificationEvent(notificationEvent);
        return notificationEvent.eventId();
    }
}
