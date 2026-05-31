package com.deliveryos.application.usecases;

import com.deliveryos.adapters.in.web.dto.response.KpiResponse;
import com.deliveryos.domain.model.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deliveryos.ports.out.DeliveryRepository;

/**
 * Use case Analytics — calcul des KPIs et métriques.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsUseCase {

    private final DeliveryRepository deliveryRepository;

    @Transactional(readOnly = true)
    public KpiResponse getKpis(String period) {
        long totalDeliveries = deliveryRepository.countByStatus(DeliveryStatus.CREATED)
                + deliveryRepository.countByStatus(DeliveryStatus.ASSIGNED)
                + deliveryRepository.countByStatus(DeliveryStatus.IN_TRANSIT)
                + deliveryRepository.countByStatus(DeliveryStatus.DELIVERED)
                + deliveryRepository.countByStatus(DeliveryStatus.FAILED)
                + deliveryRepository.countByStatus(DeliveryStatus.RETURNED);

        long deliveredCount = deliveryRepository.countByStatus(DeliveryStatus.DELIVERED);
        long failedCount = deliveryRepository.countByStatus(DeliveryStatus.FAILED);
        long createdCount = deliveryRepository.countByStatus(DeliveryStatus.CREATED);
        long inTransitCount = deliveryRepository.countByStatus(DeliveryStatus.IN_TRANSIT);

        double deliveryRate = totalDeliveries > 0
                ? (double) deliveredCount / totalDeliveries * 100
                : 0.0;

        double firstAttemptRate = totalDeliveries > 0
                ? (double) (deliveredCount - failedCount) / totalDeliveries * 100
                : 0.0;

        log.info("KPIs computed for period: {}", period);

        return KpiResponse.builder()
                .totalDeliveries(totalDeliveries)
                .deliveredCount(deliveredCount)
                .failedCount(failedCount)
                .createdCount(createdCount)
                .inTransitCount(inTransitCount)
                .deliveryRate(Math.round(deliveryRate * 10.0) / 10.0)
                .firstAttemptDeliveryRate(Math.round(firstAttemptRate * 10.0) / 10.0)
                .totalCo2Kg(0.0)
                .co2PerDelivery(0.0)
                .period(period)
                .build();
    }
}