package com.deliveryos.application.usecases;

import com.deliveryos.adapters.out.ai.AiEngineClient;
import com.deliveryos.adapters.out.ai.dto.EtaRequest;
import com.deliveryos.adapters.out.ai.dto.EtaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case ETA — prédit le temps d'arrivée estimé.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EtaUseCase {

    private final AiEngineClient aiEngineClient;

    public EtaResponse predictEta(
            UUID driverId,
            UUID tourId,
            double currentLat,
            double currentLng,
            double destLat,
            double destLng,
            int stopsRemaining) {

        LocalDateTime now = LocalDateTime.now();

        EtaRequest request = EtaRequest.builder()
                .driverId(driverId.toString())
                .tourId(tourId.toString())
                .currentLatitude(currentLat)
                .currentLongitude(currentLng)
                .destinationLatitude(destLat)
                .destinationLongitude(destLng)
                .stopsRemaining(stopsRemaining)
                .hourOfDay(now.getHour())
                .dayOfWeek(now.getDayOfWeek().getValue())
                .weather("CLEAR")
                .historicalAvgSpeedKmh(30.0)
                .build();

        EtaResponse response = aiEngineClient.predictEta(request);

        log.info("ETA prediction for driver {}: {} minutes",
                driverId, response.getEtaMinutes());

        return response;
    }
}