package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.response.KpiResponse;
import com.deliveryos.application.usecases.AnalyticsUseCase;
import com.deliveryos.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Analytics — KPIs et métriques.
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Analytics", description = "Analytics and KPI endpoints")
public class AnalyticsController {

    private final AnalyticsUseCase analyticsUseCase;

    @GetMapping("/kpis")
    @Operation(summary = "Get main KPIs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','VIEWER')")
    public ResponseEntity<ApiResponse<KpiResponse>> getKpis(
            @RequestParam(defaultValue = "MONTHLY") String period) {

        KpiResponse response = analyticsUseCase.getKpis(period);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}