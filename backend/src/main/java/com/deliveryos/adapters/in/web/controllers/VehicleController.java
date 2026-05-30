package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.request.CreateVehicleRequest;
import com.deliveryos.adapters.in.web.dto.response.VehicleResponse;
import com.deliveryos.application.usecases.VehicleUseCase;
import com.deliveryos.shared.ApiResponse;
import com.deliveryos.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fleet", description = "Vehicle management endpoints")
public class VehicleController {

    private final VehicleUseCase vehicleUseCase;

    @GetMapping
    @Operation(summary = "List all vehicles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','VIEWER')")
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var result = vehicleUseCase.findAll(PageRequest.of(page, size))
                .map(VehicleResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/available")
    @Operation(summary = "List available vehicles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER')")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> findAvailable() {
        List<VehicleResponse> vehicles = vehicleUseCase.findAvailable()
                .stream()
                .map(VehicleResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(vehicles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','VIEWER')")
    public ResponseEntity<ApiResponse<VehicleResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(VehicleResponse.from(vehicleUseCase.findById(id))));
    }

    @PostMapping
    @Operation(summary = "Create a new vehicle")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> create(
            @Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        VehicleResponse.from(vehicleUseCase.create(request)),
                        "Vehicle created successfully"));
    }

    @PatchMapping("/{id}/mileage")
    @Operation(summary = "Update vehicle mileage")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateMileage(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> body) {
        Integer mileage = body.get("mileageKm");
        if (mileage == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("mileageKm is required"));
        }
        return ResponseEntity.ok(ApiResponse.ok(
                VehicleResponse.from(vehicleUseCase.updateMileage(id, mileage))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        vehicleUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Vehicle deleted successfully"));
    }
}