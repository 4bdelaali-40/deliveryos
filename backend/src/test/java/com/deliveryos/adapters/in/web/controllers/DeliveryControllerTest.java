package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.application.usecases.DeliveryUseCase;
import com.deliveryos.config.JwtAuthenticationFilter;
import com.deliveryos.config.JwtService;
import com.deliveryos.domain.model.Delivery;
import com.deliveryos.domain.model.DeliveryPriority;
import com.deliveryos.domain.model.DeliveryStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = DeliveryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@DisplayName("DeliveryController Tests")
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeliveryUseCase deliveryUseCase;

    @MockBean
    private JwtService jwtService;

    private Delivery buildSampleDelivery() {
        return Delivery.builder()
                .id(UUID.randomUUID())
                .trackingCode("DOS-123456-ABC123")
                .status(DeliveryStatus.CREATED)
                .recipientName("John Doe")
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
    @DisplayName("GET /api/deliveries should return 200")
    @WithMockUser(roles = "DISPATCHER")
    void shouldReturnDeliveriesList() throws Exception {
        Delivery delivery = buildSampleDelivery();

        when(deliveryUseCase.findAll(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(delivery)));

        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /api/deliveries/{id} should return 200")
    @WithMockUser(roles = "DISPATCHER")
    void shouldReturnDeliveryById() throws Exception {
        Delivery delivery = buildSampleDelivery();

        when(deliveryUseCase.findById(delivery.getId())).thenReturn(delivery);

        mockMvc.perform(get("/api/deliveries/{id}", delivery.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trackingCode")
                        .value(delivery.getTrackingCode()));
    }

    @Test
    @DisplayName("GET /api/deliveries should return 401 for unauthenticated")
    void shouldReturn401ForUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isUnauthorized());
    }
}