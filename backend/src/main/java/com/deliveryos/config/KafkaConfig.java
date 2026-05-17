package com.deliveryos.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration Kafka.
 * Déclare tous les topics utilisés par l'application.
 * Les topics sont créés automatiquement au démarrage si inexistants.
 */
@Configuration
public class KafkaConfig {

    // ── Topics ───────────────────────────────────────────────

    public static final String TOPIC_DELIVERY_CREATED    = "delivery.created";
    public static final String TOPIC_DELIVERY_UPDATED    = "delivery.updated";
    public static final String TOPIC_DELIVERY_STATUS     = "delivery.status.changed";
    public static final String TOPIC_TOUR_STARTED        = "tour.started";
    public static final String TOPIC_TOUR_COMPLETED      = "tour.completed";
    public static final String TOPIC_GPS_POSITION        = "gps.position.updated";
    public static final String TOPIC_NOTIFICATION_SEND   = "notification.send";
    public static final String TOPIC_CO2_RECORDED        = "co2.recorded";

    // ── Topic Beans ──────────────────────────────────────────

    @Bean
    public NewTopic deliveryCreatedTopic() {
        return TopicBuilder.name(TOPIC_DELIVERY_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryUpdatedTopic() {
        return TopicBuilder.name(TOPIC_DELIVERY_UPDATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryStatusTopic() {
        return TopicBuilder.name(TOPIC_DELIVERY_STATUS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tourStartedTopic() {
        return TopicBuilder.name(TOPIC_TOUR_STARTED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tourCompletedTopic() {
        return TopicBuilder.name(TOPIC_TOUR_COMPLETED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic gpsPositionTopic() {
        return TopicBuilder.name(TOPIC_GPS_POSITION)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationSendTopic() {
        return TopicBuilder.name(TOPIC_NOTIFICATION_SEND)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic co2RecordedTopic() {
        return TopicBuilder.name(TOPIC_CO2_RECORDED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}