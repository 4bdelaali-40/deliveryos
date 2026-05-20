package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.out.ai.dto.VrpResponse;
import com.deliveryos.application.usecases.VrpUseCase;
import com.deliveryos.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller VRP — optimisation de routes via AI Engine.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AI Optimization", description = "Route optimization endpoints")
public class VrpController {

    private final VrpUseCase vrpUseCase;

    @PostMapping("/optimize-routes")
    @Operation(summary = "Optimize delivery routes using OR-Tools VRP")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER')")
    public ResponseEntity<ApiResponse<VrpResponse>> optimizeRoutes(
            @RequestBody OptimizeRoutesRequest request) {

        VrpResponse response = vrpUseCase.optimizeRoutes(
                request.getDate(),
                request.getVehicleIds(),
                request.getDriverIds(),
                request.getDepotLatitude(),
                request.getDepotLongitude()
        );

        return ResponseEntity.ok(
                ApiResponse.ok(response, "Routes optimized successfully"));
    }

    @Getter
    @NoArgsConstructor
    public static class OptimizeRoutesRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;
        private List<String> vehicleIds;
        private List<String> driverIds;
        private double depotLatitude;
        private double depotLongitude;
    }
}