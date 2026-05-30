package com.deliveryos.application.services;

import com.deliveryos.adapters.out.persistence.repositories.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Tâches planifiées — nettoyage, maintenance, rapports automatiques.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final RefreshTokenJpaRepository refreshTokenRepository;

    /**
     * Nettoie les refresh tokens expirés — tous les jours à 2h du matin.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanExpiredRefreshTokens() {
        log.info("Cleaning expired refresh tokens...");
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        log.info("Expired refresh tokens cleaned");
    }

    /**
     * Health check log — toutes les heures.
     */
    @Scheduled(fixedRate = 3_600_000)
    public void healthCheckLog() {
        log.info("DeliveryOS backend running — {}", Instant.now());
    }
}