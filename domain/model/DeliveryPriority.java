package com.deliveryos.domain.model;

public enum DeliveryPriority {
    NORMAL,
    URGENT,
    VIP;

    public boolean isHigherThan(DeliveryPriority other) {
        return this.ordinal() > other.ordinal();
    }
}