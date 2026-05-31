package com.deliveryos.application.usecases;

import com.deliveryos.adapters.in.web.dto.request.CreateDeliveryRequest;
import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.deliveryos.ports.out.DeliveryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryUseCase Tests")
class DeliveryUseCaseTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryUseCase deliveryUseCase;

    private Delivery sampleDelivery;

    @BeforeEach
    void setUp() {
        sampleDelivery = Delivery.builder()
                .id(UUID.randomUUID())
                .trackingCode("DOS-123456-ABC123")
                .status(DeliveryStatus.CREATED)
                .recipientName("John Doe")
                .recipientEmail("john@example.com")
                .address("123 Main St")
                .city("Paris")
                .postalCode("75001")
                .priority(DeliveryPriority.NORMAL)
                .scheduledDate(LocalDate.now())
                .attemptCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should create delivery successfully")
    void shouldCreateDeliverySuccessfully() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(sampleDelivery);

        CreateDeliveryRequest request = mock(CreateDeliveryRequest.class);
        when(request.getRecipientName()).thenReturn("John Doe");
        when(request.getAddress()).thenReturn("123 Main St");
        when(request.getCity()).thenReturn("Paris");
        when(request.getPostalCode()).thenReturn("75001");
        when(request.getPriority()).thenReturn(DeliveryPriority.NORMAL);
        when(request.getScheduledDate()).thenReturn(LocalDate.now());

        Delivery result = deliveryUseCase.create(request, null);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CREATED);
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    @DisplayName("Should find delivery by id")
    void shouldFindDeliveryById() {
        when(deliveryRepository.findById(sampleDelivery.getId()))
                .thenReturn(Optional.of(sampleDelivery));

        Delivery result = deliveryUseCase.findById(sampleDelivery.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleDelivery.getId());
        assertThat(result.getTrackingCode()).isEqualTo(sampleDelivery.getTrackingCode());
    }

    @Test
    @DisplayName("Should throw when delivery not found")
    void shouldThrowWhenDeliveryNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(deliveryRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryUseCase.findById(unknownId))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    @Test
    @DisplayName("Should transition status correctly")
    void shouldTransitionStatusCorrectly() {
        Delivery assignedDelivery = sampleDelivery
                .withStatus(DeliveryStatus.ASSIGNED);

        when(deliveryRepository.findById(sampleDelivery.getId()))
                .thenReturn(Optional.of(sampleDelivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(assignedDelivery);

        Delivery result = deliveryUseCase.updateStatus(
                sampleDelivery.getId(), DeliveryStatus.ASSIGNED);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
    }

    @Test
    @DisplayName("Should throw on invalid status transition")
    void shouldThrowOnInvalidStatusTransition() {
        when(deliveryRepository.findById(sampleDelivery.getId()))
                .thenReturn(Optional.of(sampleDelivery));

        assertThatThrownBy(() ->
                deliveryUseCase.updateStatus(
                        sampleDelivery.getId(), DeliveryStatus.DELIVERED))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should delete only CREATED deliveries")
    void shouldDeleteOnlyCreatedDeliveries() {
        when(deliveryRepository.findById(sampleDelivery.getId()))
                .thenReturn(Optional.of(sampleDelivery));

        deliveryUseCase.delete(sampleDelivery.getId());

        verify(deliveryRepository, times(1)).deleteById(sampleDelivery.getId());
    }

    @Test
    @DisplayName("Should not delete non-CREATED delivery")
    void shouldNotDeleteNonCreatedDelivery() {
        Delivery deliveredDelivery = sampleDelivery
                .withStatus(DeliveryStatus.DELIVERED);

        when(deliveryRepository.findById(deliveredDelivery.getId()))
                .thenReturn(Optional.of(deliveredDelivery));

        assertThatThrownBy(() ->
                deliveryUseCase.delete(deliveredDelivery.getId()))
                .isInstanceOf(IllegalStateException.class);

        verify(deliveryRepository, never()).deleteById(any());
    }
}