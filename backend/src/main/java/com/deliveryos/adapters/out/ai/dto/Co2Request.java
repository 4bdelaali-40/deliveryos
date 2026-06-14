package com.deliveryos.adapters.out.ai.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO envoyé au Python AI Engine pour la prédiction CO2.
 */
@Getter
@Builder
public class Co2Request {

    private final double distanceKm;
    private final String vehicleType;
    private final double loadKg;
    private final double avgSpeedKmh;
    private final String roadType;
    private final String weather;
    private final int hourOfDay;
    private final int dayOfWeek;
    private final boolean isUrban;
}