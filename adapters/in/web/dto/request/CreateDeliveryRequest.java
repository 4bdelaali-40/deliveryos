package com.deliveryos.adapters.in.web.dto.request;

import com.deliveryos.domain.model.DeliveryPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CreateDeliveryRequest {

    @NotBlank(message = "Recipient name is required")
    @Size(max = 200, message = "Recipient name must not exceed 200 characters")
    private String recipientName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String recipientPhone;

    private String recipientEmail;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @Positive(message = "Weight must be positive")
    private Double weightKg;

    @Positive(message = "Volume must be positive")
    private Double volumeM3;

    @NotNull(message = "Priority is required")
    private DeliveryPriority priority;

    private LocalTime timeWindowStart;
    private LocalTime timeWindowEnd;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}