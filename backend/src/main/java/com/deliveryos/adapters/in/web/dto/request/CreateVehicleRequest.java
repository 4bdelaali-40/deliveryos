package com.deliveryos.adapters.in.web.dto.request;

import com.deliveryos.domain.model.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateVehicleRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotNull(message = "Vehicle type is required")
    private Vehicle.Type type;

    private String brand;
    private String model;
    private Integer year;

    @NotNull(message = "Capacity kg is required")
    @Positive(message = "Capacity must be positive")
    private Double capacityKg;

    @NotNull(message = "Capacity m3 is required")
    @Positive(message = "Capacity must be positive")
    private Double capacityM3;

    @NotNull(message = "CO2 per km is required")
    private Double co2PerKm;

    private Vehicle.FuelType fuelType;
    private LocalDate lastRevisionDate;
    private Integer nextRevisionKm;
}