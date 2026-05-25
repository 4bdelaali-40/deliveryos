package com.deliveryos.domain.model;

public enum DeliveryStatus {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    FAILED,
    RETURNED;

    public boolean canTransitionTo(DeliveryStatus next) {
        return switch (this) {
            case CREATED    -> next == ASSIGNED;
            case ASSIGNED   -> next == PICKED_UP || next == CREATED;
            case PICKED_UP  -> next == IN_TRANSIT;
            case IN_TRANSIT -> next == DELIVERED || next == FAILED;
            case FAILED     -> next == RETURNED || next == ASSIGNED;
            case DELIVERED, RETURNED -> false;
        };
    }
}