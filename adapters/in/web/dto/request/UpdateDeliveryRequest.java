package adapters.in.web.dto.request;

import com.deliveryos.domain.model.DeliveryPriority;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class UpdateDeliveryRequest {

    @Size(max = 200, message = "Recipient name must not exceed 200 characters")
    private String recipientName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String recipientPhone;

    private String recipientEmail;
    private String address;
    private String city;
    private String postalCode;

    @Positive(message = "Weight must be positive")
    private Double weightKg;

    @Positive(message = "Volume must be positive")
    private Double volumeM3;

    private DeliveryPriority priority;
    private LocalTime timeWindowStart;
    private LocalTime timeWindowEnd;
    private LocalDate scheduledDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}