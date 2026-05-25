package com.deliveryos.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service de tracking de la flotte — lit les positions depuis Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FleetTrackingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String GPS_KEY_PREFIX = "gps:driver:";

    public List<Object> getAllActivePositions() {
        Set<String> keys = redisTemplate.keys(GPS_KEY_PREFIX + "*");
        List<Object> positions = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                Object position = redisTemplate.opsForValue().get(key);
                if (position != null) {
                    positions.add(position);
                }
            }
        }

        return positions;
    }

    public Object getDriverPosition(UUID driverId) {
        String key = GPS_KEY_PREFIX + driverId;
        return redisTemplate.opsForValue().get(key);
    }

    public void clearDriverPosition(UUID driverId) {
        String key = GPS_KEY_PREFIX + driverId;
        redisTemplate.delete(key);
        log.info("GPS position cleared for driver: {}", driverId);
    }
}