package de.hofuniversity.assemblyplanner.persistence.model.notification;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderNotificationPayload implements NotificationPayload, Serializable {
    private UUID orderId;
    private UUID customerId;

    public OrderNotificationPayload(UUID orderId, UUID customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public OrderNotificationPayload(){}

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderNotificationPayload that = (OrderNotificationPayload) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId);
    }
}
