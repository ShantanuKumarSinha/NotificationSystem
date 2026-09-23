package com.shann.notificationsystem.producer;

import com.shann.notificationsystem.entity.FailedMessage;
import com.shann.notificationsystem.model.NotificationEvent;
import com.shann.notificationsystem.repository.FailedMessageRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationProducer {

    private static final String TOPIC="notification.events";
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private FailedMessageRepository failedMessageRepository;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate, FailedMessageRepository failedMessageRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedMessageRepository = failedMessageRepository;
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

    // Scheduled job or manual trigger
    @Scheduled(fixedDelay = 60000)
    public void replayFailedMessages() {
        List<FailedMessage> failedMessages = failedMessageRepository.findByReplayedFalse();

        for (FailedMessage msg : failedMessages) {
            try {
                // Re-publish to original topic
                Map<String, String> metadata = new HashMap<>();
                if (msg.getMetadataKey() != null && msg.getMetadataValue() != null) {
                    metadata.put(msg.getMetadataKey(), msg.getMetadataValue());
                }
                NotificationEvent notificationEvent = new NotificationEvent(msg.getId(), msg.getUserId(), msg.getPayload(), msg.getChannel(), msg.getOriginalTopic(),msg.getBody(), metadata, msg.getCreatedAt());
                kafkaTemplate.send(msg.getOriginalTopic(), notificationEvent);

                // Mark as replayed
                msg.setReplayed(true);
                failedMessageRepository.save(msg);
            } catch (Exception e) {
                System.err.printf("Replay failed for message {}", msg.getId(), e);
            }
        }
    }


}
