package com.shann.notificationsystem.producer;

import com.shann.notificationsystem.model.NotificationEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationProducer {

    private static final String TOPIC="notification.events";
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<Void> sendNotificationEvent(NotificationEvent event) {
        ProducerRecord<String, NotificationEvent> record = new ProducerRecord<>(TOPIC, event.eventId(), event);
        record.headers().add(new RecordHeader("eventId", event.eventId().getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("channel", event.channel().name().getBytes(StandardCharsets.UTF_8)));

        return kafkaTemplate.send(record).toCompletableFuture().
                thenAccept(result -> {
                    System.out.println("Notification event sent successfully: " + event.eventId());
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to send notification event: " + ex.getMessage());
                    return null;
                });
    }
}
