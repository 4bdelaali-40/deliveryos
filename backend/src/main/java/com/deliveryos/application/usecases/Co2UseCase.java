package com.deliveryos.application.usecases;

import com.deliveryos.adapters.out.ai.AiEngineClient;
import com.deliveryos.adapters.out.ai.dto.Co2Request;
import com.deliveryos.adapters.out.ai.dto.Co2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Use case CO2 — prédiction des émissions carbone.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Co2UseCase {

    private final AiEngineClient aiEngineClient;

    public Co2Response predictCo2(
            double distanceKm,
            String vehicleType,
            double loadKg,
            double avgSpeedKmh,
            String roadType) {

        LocalDateTime now = LocalDateTime.now();

        Co2Request request = Co2Request.builder()
                .distanceKm(distanceKm)
                .vehicleType(vehicleType)
                .loadKg(loadKg)
                .avgSpeedKmh(avgSpeedKmh)
                .roadType(roadType)
                .weather("CLEAR")
                .hourOfDay(now.getHour())
                .dayOfWeek(now.getDayOfWeek().getValue())
                .isUrban(roadType.equals("URBAN"))
                .build();

        Co2Response response = aiEngineClient.predictCo2(request);

        log.info("CO2 prediction: {}kg for {}km with {}",
                response.getCo2Kg(), distanceKm, vehicleType);

        return response;
    }
}