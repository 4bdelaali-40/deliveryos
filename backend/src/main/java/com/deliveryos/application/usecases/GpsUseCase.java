package com.deliveryos.application.usecases;

import com.deliveryos.domain.model.GpsPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Use case GPS — reçoit et diffuse les positions GPS en temps réel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GpsUseCase {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GPS_KEY_PREFIX = "gps:driver:";
    private static final Duration GPS_TTL = Duration.ofHours(24);

    public void updatePosition(GpsPosition position) {
        if (!position.isValid()) {
            log.warn("Invalid GPS position for driver: {}", position.getDriverId());
            return;
        }

        // Stocker dans Redis
        String key = GPS_KEY_PREFIX + position.getDriverId();
        redisTemplate.opsForValue().set(key, buildPositionPayload(position), GPS_TTL);

        // Diffuser via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/fleet",
                buildPositionPayload(position)
        );

        // Diffuser à la tournée spécifique si disponible
        if (position.getTourId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/tour/" + position.getTourId(),
                    buildPositionPayload(position)
            );
        }

        log.debug("GPS position updated for driver: {}", position.getDriverId());
    }

    public Object getLastPosition(UUID driverId) {
        String key = GPS_KEY_PREFIX + driverId;
        return redisTemplate.opsForValue().get(key);
    }

    private java.util.Map<String, Object> buildPositionPayload(GpsPosition position) {
        return java.util.Map.of(
                "driverId", position.getDriverId().toString(),
                "latitude", position.getLatitude(),
                "longitude", position.getLongitude(),
                "speedKmh", position.getSpeedKmh() != null ? position.getSpeedKmh() : 0.0,
                "heading", position.getHeading() != null ? position.getHeading() : 0.0,
                "recordedAt", position.getRecordedAt().toString()
        );
    }
}