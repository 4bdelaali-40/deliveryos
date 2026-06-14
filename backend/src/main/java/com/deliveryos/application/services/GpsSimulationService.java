package com.deliveryos.application.services;

import com.deliveryos.adapters.out.persistence.entities.UserEntity;
import com.deliveryos.adapters.out.persistence.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simule le deplacement GPS des chauffeurs et publie les positions
 * sur /topic/fleet via STOMP, pour alimenter la page Live Tracking.
 *
 * A desactiver en production (profil "demo" uniquement) en remplacant
 * par de vraies remontees GPS depuis l'application mobile des chauffeurs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GpsSimulationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserJpaRepository userJpaRepository;

    // Position courante simulee par chauffeur
    private final Map<UUID, SimulatedPosition> driverPositions = new ConcurrentHashMap<>();

    // Centre approximatif de la zone de livraison (Paris)
    private static final double CENTER_LAT = 48.8566;
    private static final double CENTER_LNG = 2.3522;
    private static final double RADIUS = 0.06; // ~6km autour du centre

    /**
     * Publie une nouvelle position GPS pour chaque chauffeur actif toutes les 5 secondes.
     */
    @Scheduled(fixedRate = 5000)
    public void simulateFleetMovement() {
        List<UserEntity> drivers = userJpaRepository.findByRole(
                "DRIVER",
                org.springframework.data.domain.PageRequest.of(0, 50)
        ).getContent();

        if (drivers.isEmpty()) {
            return;
        }

        for (UserEntity driver : drivers) {
            SimulatedPosition pos = driverPositions.computeIfAbsent(
                    driver.getId(),
                    id -> SimulatedPosition.randomNear(CENTER_LAT, CENTER_LNG, RADIUS)
            );

            pos.step();

            Map<String, Object> payload = new HashMap<>();
            payload.put("driverId", driver.getId().toString());
            payload.put("latitude", pos.lat);
            payload.put("longitude", pos.lng);
            payload.put("speedKmh", pos.speedKmh);
            payload.put("heading", pos.heading);
            payload.put("recordedAt", Instant.now().toString());

            messagingTemplate.convertAndSend("/topic/fleet", payload);
        }

        log.debug("Published GPS positions for {} drivers", drivers.size());
    }

    /**
     * Etat de position simulee avec deplacement aleatoire continu (random walk).
     */
    private static class SimulatedPosition {
        double lat;
        double lng;
        double heading;
        double speedKmh;

        static SimulatedPosition randomNear(double centerLat, double centerLng, double radius) {
            SimulatedPosition p = new SimulatedPosition();
            p.lat = centerLat + (Math.random() - 0.5) * radius;
            p.lng = centerLng + (Math.random() - 0.5) * radius;
            p.heading = Math.random() * 360;
            p.speedKmh = 20 + Math.random() * 40;
            return p;
        }

        void step() {
            // Deviation legere de direction
            heading += (Math.random() - 0.5) * 30;
            if (heading < 0) heading += 360;
            if (heading >= 360) heading -= 360;

            // Vitesse variable
            speedKmh = Math.max(5, Math.min(60, speedKmh + (Math.random() - 0.5) * 10));

            // Deplacement proportionnel a la vitesse (echelle approximative pour 5s)
            double distanceDeg = (speedKmh / 3600.0) * 5 * 0.01;
            double rad = Math.toRadians(heading);
            lat += Math.cos(rad) * distanceDeg;
            lng += Math.sin(rad) * distanceDeg;
        }
    }
}
