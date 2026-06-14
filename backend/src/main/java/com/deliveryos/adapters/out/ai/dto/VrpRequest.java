package com.deliveryos.adapters.out.ai.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * DTO envoyé au Python AI Engine pour l'optimisation VRP.
 */
@Getter
@Builder
public class VrpRequest {

    private final List<DeliveryPoint> deliveries;
    private final List<VehicleDto> vehicles;
    private final String date;
    private final String optimizeFor;

    @Getter
    @Builder
    public static class DeliveryPoint {
        private final String id;
        private final String address;
        private final double latitude;
        private final double longitude;
        private final double weightKg;
        private final double volumeM3;
        private final String priority;
        private final String timeWindowStart;
        private final String timeWindowEnd;
        private final int serviceTimeMin;
    }

    @Getter
    @Builder
    public static class VehicleDto {
        private final String id;
        private final String driverId;
        private final double capacityKg;
        private final double capacityM3;
        private final double startLatitude;
        private final double startLongitude;
        private final int maxDurationMin;
    }
}