package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité domaine Badge — gamification drivers.
 */
@Getter
@Builder
public class Badge {

    public enum Type {
        ECO_CHAMPION,
        ZERO_DELAY,
        TOP_DELIVERY,
        PERFECT_WEEK,
        CO2_SAVER,
        SPEED_STAR,
        RELIABILITY_KING
    }

    private final UUID id;
    private final UUID driverId;
    private final Type type;
    private final String period;
    private final Instant earnedAt;

    public String getDisplayName() {
        return switch (type) {
            case ECO_CHAMPION -> "Eco Champion";
            case ZERO_DELAY -> "Zero Delay";
            case TOP_DELIVERY -> "Top Delivery";
            case PERFECT_WEEK -> "Perfect Week";
            case CO2_SAVER -> "CO2 Saver";
            case SPEED_STAR -> "Speed Star";
            case RELIABILITY_KING -> "Reliability King";
        };
    }
}