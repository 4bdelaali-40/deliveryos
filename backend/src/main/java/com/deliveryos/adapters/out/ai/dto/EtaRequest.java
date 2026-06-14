package com.deliveryos.adapters.out.ai.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EtaRequest {
    private final String driverId;
    private final String tourId;
    private final double currentLatitude;
    private final double currentLongitude;
    private final double destinationLatitude;
    private final double destinationLongitude;
    private final int stopsRemaining;
    private final int hourOfDay;
    private final int dayOfWeek;
    private final String weather;
    private final double historicalAvgSpeedKmh;
}