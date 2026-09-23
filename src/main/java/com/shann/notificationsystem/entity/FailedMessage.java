package com.shann.notificationsystem.entity;

import com.shann.notificationsystem.model.Channel;
import jakarta.persistence.*;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class FailedMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  private String userId;
  private String payload;
  private Channel channel;
  private String originalTopic;
  private String body;
  private String metadataKey;
  private String metadataValue;
  private Instant createdAt;
  private Long originalOffset;
  private String exceptionMessage;
  private boolean replayed;
}
