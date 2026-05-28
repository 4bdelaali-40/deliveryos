package com.deliveryos.adapters.in.web.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO réponse — KPIs du dashboard analytique.
 */
@Getter
@Builder
public class KpiResponse {

    private final long totalDeliveries;
    private final long deliveredCount;
    private final long failedCount;
    private final long createdCount;
    private final long inTransitCount;
    private final double deliveryRate;
    private final double firstAttemptDeliveryRate;
    private final double totalCo2Kg;
    private final double co2PerDelivery;
    private final String period;
}