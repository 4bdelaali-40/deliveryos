package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité domaine GpsPosition — pure, sans annotation JPA.
 */
@Getter
@Builder
public class GpsPosition {

    private final UUID driverId;
    private final UUID tourId;
    private final double latitude;
    private final double longitude;
    private final Double speedKmh;
    private final Double heading;
    private final Instant recordedAt;

    public boolean isValid() {
        return latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }
}