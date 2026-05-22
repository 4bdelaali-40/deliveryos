package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.out.ai.dto.Co2Response;
import com.deliveryos.application.usecases.Co2UseCase;
import com.deliveryos.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller CO2 — prédiction des émissions carbone.
 */
@RestController
@RequestMapping("/api/co2")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "CO2", description = "Carbon emission prediction endpoints")
public class Co2Controller {

    private final Co2UseCase co2UseCase;

    @PostMapping("/predict")
    @Operation(summary = "Predict CO2 emissions for a route")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER')")
    public ResponseEntity<ApiResponse<Co2Response>> predictCo2(
            @RequestBody Co2PredictRequest request) {

        Co2Response response = co2UseCase.predictCo2(
                request.getDistanceKm(),
                request.getVehicleType(),
                request.getLoadKg(),
                request.getAvgSpeedKmh(),
                request.getRoadType()
        );

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Getter
    @NoArgsConstructor
    public static class Co2PredictRequest {
        private double distanceKm;
        private String vehicleType;
        private double loadKg;
        private double avgSpeedKmh;
        private String roadType;
    }
}