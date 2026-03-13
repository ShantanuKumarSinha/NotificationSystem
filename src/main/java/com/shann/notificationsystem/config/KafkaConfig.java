package com.shann.notificationsystem.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template){
        var backOff = new ExponentialBackOffWithMaxRetries(5);
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000);
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,((consumerRecord, e) -> new TopicPartition(consumerRecord.topic()+".DLT", consumerRecord.partition())));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
