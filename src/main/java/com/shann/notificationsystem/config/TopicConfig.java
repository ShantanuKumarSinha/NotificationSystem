package com.shann.notificationsystem.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notification.events")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationsDltTopic() {
        return TopicBuilder.name("notification.events.DLT")
                .partitions(2)
                .replicas(1)
                .build();
    }

}
