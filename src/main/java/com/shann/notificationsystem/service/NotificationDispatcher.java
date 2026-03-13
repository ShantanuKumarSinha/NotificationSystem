package com.shann.notificationsystem.service;

import com.shann.notificationsystem.model.NotificationEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatcher {

    public void dispatch(NotificationEvent notificationEvent) {
        // In a real implementation, this would send the message to Kafka
        switch (notificationEvent.channel()) {
            case EMAIL -> sendEmail(notificationEvent);
            case SMS -> sendSms(notificationEvent);
            case PUSH_NOTIFICATION -> sendPushNotification(notificationEvent);
        }
    }

    private void sendPushNotification(NotificationEvent notificationEvent) {
        System.out.println("Sending push notification to user " + notificationEvent.userId() + ": " + notificationEvent.title() + " - " + notificationEvent.body());
    }

    private void sendSms(NotificationEvent notificationEvent) {
        System.out.println("Sending SMS to user " + notificationEvent.userId() + ": " + notificationEvent.title() + " - " + notificationEvent.body());
    }

    private void sendEmail(NotificationEvent notificationEvent) {
        System.out.println("Sending email to user " + notificationEvent.userId() + ": " + notificationEvent.title() + " - " + notificationEvent.body());
    }
}
