package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.application.usecases.GpsUseCase;
import com.deliveryos.domain.model.GpsPosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Controller WebSocket GPS — reçoit les positions des drivers.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GpsTrackingController {

    private final GpsUseCase gpsUseCase;

    /**
     * Reçoit les positions GPS via WebSocket STOMP.
     * Le driver envoie vers /app/gps/update
     */
    @MessageMapping("/gps/update")
    public void updatePosition(@Payload GpsUpdateMessage message) {
        GpsPosition position = GpsPosition.builder()
                .driverId(UUID.fromString(message.getDriverId()))
                .tourId(message.getTourId() != null
                        ? UUID.fromString(message.getTourId()) : null)
                .latitude(message.getLatitude())
                .longitude(message.getLongitude())
                .speedKmh(message.getSpeedKmh())
                .heading(message.getHeading())
                .recordedAt(Instant.now())
                .build();

        gpsUseCase.updatePosition(position);
    }

    @Getter
    @NoArgsConstructor
    public static class GpsUpdateMessage {
        private String driverId;
        private String tourId;
        private double latitude;
        private double longitude;
        private Double speedKmh;
        private Double heading;
    }
}