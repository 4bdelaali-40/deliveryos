package com.deliveryos.domain.model;

/**
 * Rôles disponibles dans le système RBAC.
 * Chaque rôle hérite des permissions du rôle précédent.
 *
 * VIEWER < DRIVER < DISPATCHER < ADMIN < SUPER_ADMIN
 */
public enum Role {
    SUPER_ADMIN,
    ADMIN,
    DISPATCHER,
    DRIVER,
    VIEWER
}