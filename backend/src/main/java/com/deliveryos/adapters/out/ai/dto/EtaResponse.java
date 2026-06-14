package com.deliveryos.adapters.out.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EtaResponse {
    private String driverId;
    private String tourId;
    private int etaMinutes;
    private String etaTimestamp;
    private double confidence;
    private String modelVersion;
}