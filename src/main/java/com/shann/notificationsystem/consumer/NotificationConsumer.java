package com.shann.notificationsystem.consumer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shann.notificationsystem.model.NotificationEvent;
import com.shann.notificationsystem.service.NotificationDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class NotificationConsumer {

    private final Cache<String, Boolean> processedEventsCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(10000)
            .build();
    private NotificationDispatcher notificationDispatcher;

    public NotificationConsumer(NotificationDispatcher notificationDispatcher) {
        this.notificationDispatcher = notificationDispatcher;
    }

    @KafkaListener(topics = "${topics.notifiications}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<String, NotificationEvent> record) {
        var notificationEvent = record.value();
        var eventId = notificationEvent.eventId();


        if (processedEventsCache.getIfPresent(eventId) != null) {
            System.out.println("Duplicate event detected: " + eventId + ", skipping processing.");
            return;
        }

        System.out.println("Processing notification event: " + eventId + " for user " + record.value().userId() + " on channel " + record.value().channel());
        notificationDispatcher.dispatch(notificationEvent);
        processedEventsCache.put(eventId, Boolean.TRUE);
    }

    @KafkaListener(topics = "${topics.notifications-dlt}", groupId = "${spring.kafka.consumer.group-id}-dlt")
    public void onDlt(ConsumerRecord<String, NotificationEvent> record) {
        System.err.printf("Message moved to DLT: %s, eventId: %s, userId: %s, channel: %s%n",
                record.value(), record.value().eventId(), record.value().userId(), record.value().channel());
    }


}
