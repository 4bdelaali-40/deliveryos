package com.deliveryos.adapters.out.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO reçu du Python AI Engine après optimisation VRP.
 */
@Getter
@NoArgsConstructor
public class VrpResponse {

    private List<TourResult> tours;
    private double totalDistanceKm;
    private double totalCo2Kg;
    private List<String> unassignedDeliveryIds;
    private int executionMs;
    private String solverStatus;

    @Getter
    @NoArgsConstructor
    public static class TourResult {
        private String vehicleId;
        private String driverId;
        private List<StopResult> stops;
        private double totalDistanceKm;
        private double totalCo2Kg;
        private int totalDurationMin;
    }

    @Getter
    @NoArgsConstructor
    public static class StopResult {
        private String deliveryId;
        private int stopOrder;
        private String eta;
        private double distanceFromPrevKm;
        private double co2FromPrevKg;
    }
}