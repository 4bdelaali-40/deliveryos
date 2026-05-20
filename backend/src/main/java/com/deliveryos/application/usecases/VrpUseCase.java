package com.deliveryos.application.usecases;

import com.deliveryos.adapters.out.ai.AiEngineClient;
import com.deliveryos.adapters.out.ai.dto.VrpRequest;
import com.deliveryos.adapters.out.ai.dto.VrpResponse;
import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryStatus;
import com.deliveryos.ports.out.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Use case VRP — orchestre l'optimisation de routes.
 * Récupère les livraisons, construit la requête AI, retourne les tournées.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VrpUseCase {

    private final AiEngineClient aiEngineClient;
    private final DeliveryRepository deliveryRepository;

    public VrpResponse optimizeRoutes(LocalDate date, List<String> vehicleIds,
                                      List<String> driverIds, double depotLat,
                                      double depotLng) {

        // Récupère les livraisons du jour
        List<Delivery> deliveries = deliveryRepository
                .findByScheduledDateAndStatus(date, DeliveryStatus.CREATED);

        if (deliveries.isEmpty()) {
            throw new IllegalStateException(
                    "No deliveries found for date: " + date);
        }

        log.info("Optimizing routes for {} deliveries on {}", deliveries.size(), date);

        // Construit les points de livraison
        List<VrpRequest.DeliveryPoint> points = deliveries.stream()
                .map(d -> VrpRequest.DeliveryPoint.builder()
                        .id(d.getId().toString())
                        .address(d.getAddress())
                        .latitude(d.getLatitude() != null ? d.getLatitude() : depotLat)
                        .longitude(d.getLongitude() != null ? d.getLongitude() : depotLng)
                        .weightKg(d.getWeightKg() != null ? d.getWeightKg() : 0.0)
                        .volumeM3(d.getVolumeM3() != null ? d.getVolumeM3() : 0.0)
                        .priority(d.getPriority().name())
                        .timeWindowStart(d.getTimeWindowStart() != null
                                ? d.getTimeWindowStart().toString() : null)
                        .timeWindowEnd(d.getTimeWindowEnd() != null
                                ? d.getTimeWindowEnd().toString() : null)
                        .serviceTimeMin(5)
                        .build())
                .toList();

        // Construit les véhicules
        List<VrpRequest.VehicleDto> vehicles = vehicleIds.stream()
                .map(vehicleId -> VrpRequest.VehicleDto.builder()
                        .id(vehicleId)
                        .driverId(driverIds.get(vehicleIds.indexOf(vehicleId)))
                        .capacityKg(500.0)
                        .capacityM3(2.5)
                        .startLatitude(depotLat)
                        .startLongitude(depotLng)
                        .maxDurationMin(480)
                        .build())
                .toList();

        VrpRequest request = VrpRequest.builder()
                .deliveries(points)
                .vehicles(vehicles)
                .date(date.toString())
                .optimizeFor("DISTANCE")
                .build();

        return aiEngineClient.optimizeRoutes(request);
    }
}