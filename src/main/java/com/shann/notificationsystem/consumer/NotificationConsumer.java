package com.shann.notificationsystem.consumer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shann.notificationsystem.entity.FailedMessage;
import com.shann.notificationsystem.model.NotificationEvent;
import com.shann.notificationsystem.repository.FailedMessageRepository;
import com.shann.notificationsystem.service.NotificationDispatcher;
import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

  private final Cache<String, Boolean> processedEventsCache =
      Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).maximumSize(10000).build();
  private NotificationDispatcher notificationDispatcher;

  private FailedMessageRepository failedMessageRepository;

  public NotificationConsumer(
      NotificationDispatcher notificationDispatcher,
      FailedMessageRepository failedMessageRepository) {
    this.notificationDispatcher = notificationDispatcher;
    this.failedMessageRepository = failedMessageRepository;
  }

  @KafkaListener(topics = "${topics.notifiications}", groupId = "${spring.kafka.consumer.group-id}")
  public void onMessage(ConsumerRecord<String, NotificationEvent> record) {
    var notificationEvent = record.value();
    var eventId = notificationEvent.eventId();

    if (processedEventsCache.getIfPresent(eventId) != null) {
      System.out.println("Duplicate event detected: " + eventId + ", skipping processing.");
      return;
    }

    System.out.println(
        "Processing notification event: "
            + eventId
            + " for user "
            + record.value().userId()
            + " on channel "
            + record.value().channel());
    notificationDispatcher.dispatch(notificationEvent);
    processedEventsCache.put(eventId, Boolean.TRUE);
  }

  @KafkaListener(
      topics = "${topics.notifications-dlt}",
      groupId = "${spring.kafka.consumer.group-id}-dlt")
  public void onDlt(ConsumerRecord<String, NotificationEvent> record) {
    System.err.printf(
        "Message moved to DLT: %s, eventId: %s, userId: %s, channel: %s%n",
        record.value(),
        record.value().eventId(),
        record.value().userId(),
        record.value().channel());
    // Optionally persist for replay
    var failed =
        FailedMessage.builder()
            .payload(record.value().toString())
            .originalTopic(record.topic())
            .originalOffset(record.offset())
            .userId(record.value().userId())
            .channel(record.value().channel())
            .body(record.value().body())
            .metadataKey(
                record.value().metadata() != null
                    ? record.value().metadata().keySet().toString()
                    : null)
            .metadataValue(
                record.value().metadata() != null
                    ? record.value().metadata().values().toString()
                    : null)
            .createdAt(record.value().createdAt())
            .exceptionMessage(
                new String(record.headers().lastHeader("kafka_dlt-exception-message").value()))
            .replayed(false)
            .build();

    failedMessageRepository.save(failed);
  }
}
