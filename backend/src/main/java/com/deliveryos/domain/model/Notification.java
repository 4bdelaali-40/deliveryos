package com.deliveryos.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité domaine Notification — pure, sans annotation JPA.
 */
@Getter
@Builder
@With
public class Notification {

    public enum Type { ALERT, INFO, WARNING, SUCCESS }
    public enum Channel { IN_APP, EMAIL, SMS }

    private final UUID id;
    private final UUID userId;
    private final Type type;
    private final Channel channel;
    private final String title;
    private final String message;
    private final boolean read;
    private final Instant readAt;
    private final Instant createdAt;

    public boolean isUnread() {
        return !read;
    }

    public Notification markAsRead() {
        return this.withRead(true).withReadAt(Instant.now());
    }
}