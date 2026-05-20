package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.request.CreateDeliveryRequest;
import com.deliveryos.adapters.in.web.dto.request.UpdateDeliveryRequest;
import com.deliveryos.adapters.in.web.dto.response.DeliveryResponse;
import com.deliveryos.application.usecases.DeliveryUseCase;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import com.deliveryos.shared.ApiResponse;
import com.deliveryos.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deliveries", description = "Delivery management endpoints")
public class DeliveryController {

    private final DeliveryUseCase deliveryUseCase;

    @GetMapping
    @Operation(summary = "List deliveries with filters and pagination")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<PageResponse<DeliveryResponse>>> findAll(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduledDate,
            @RequestParam(required = false) DeliveryPriority priority,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<DeliveryResponse> result = deliveryUseCase
                .findAll(status, scheduledDate, priority, city, pageable)
                .map(DeliveryResponse::from);

        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery by ID")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<DeliveryResponse>> findById(
            @PathVariable UUID id) {

        DeliveryResponse response = DeliveryResponse.from(
                deliveryUseCase.findById(id));

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/tracking/{code}")
    @Operation(summary = "Get delivery by tracking code")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<DeliveryResponse>> findByTrackingCode(
            @PathVariable String code) {

        DeliveryResponse response = DeliveryResponse.from(
                deliveryUseCase.findByTrackingCode(code));

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Create a new delivery")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER')")
    public ResponseEntity<ApiResponse<DeliveryResponse>> create(
            @Valid @RequestBody CreateDeliveryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        DeliveryResponse response = DeliveryResponse.from(
                deliveryUseCase.create(request, null));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Delivery created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a delivery")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER')")
    public ResponseEntity<ApiResponse<DeliveryResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDeliveryRequest request) {

        DeliveryResponse response = DeliveryResponse.from(
                deliveryUseCase.update(id, request));

        return ResponseEntity.ok(ApiResponse.ok(response, "Delivery updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update delivery status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER')")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("status field is required"));
        }

        DeliveryStatus newStatus;
        try {
            newStatus = DeliveryStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Invalid status: " + statusStr));
        }

        DeliveryResponse response = DeliveryResponse.from(
                deliveryUseCase.updateStatus(id, newStatus));

        return ResponseEntity.ok(ApiResponse.ok(response, "Status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a delivery — only CREATED status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deliveryUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Delivery deleted successfully"));
    }
}