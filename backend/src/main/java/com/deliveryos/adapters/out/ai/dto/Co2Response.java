package com.deliveryos.adapters.out.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO reçu du Python AI Engine après prédiction CO2.
 */
@Getter
@NoArgsConstructor
public class Co2Response {

    private double co2Grams;
    private double co2Kg;
    private double co2PerKm;
    private String vehicleType;
    private double distanceKm;
    private String modelVersion;
}