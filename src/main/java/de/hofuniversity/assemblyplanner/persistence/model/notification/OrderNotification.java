package de.hofuniversity.assemblyplanner.persistence.model.notification;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
public class OrderNotification extends Notification<OrderNotificationPayload> {

    @Embedded
    private OrderNotificationPayload payload;

    public enum Type { CREATED, UPDATED }

    public OrderNotification(Iterable<Employee> recipients, Type type, OrderNotificationPayload payload) {
        super(recipients, type.name());
        this.payload = payload;
    }

    public OrderNotification(Iterable<Employee> recipients, Type type, UUID orderId, UUID customerId) {
        this(recipients, type, new OrderNotificationPayload(orderId, customerId));
    }

    public OrderNotification(){}

    public OrderNotification withPayload(OrderNotificationPayload payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public OrderNotification withRecipients(Iterable<Employee> recipients) {
        super.withRecipients(recipients);
        return this;
    }

    public OrderNotification withType(Type type) {
        this.setType(type.name());
        return this;
    }

    @Override
    public OrderNotificationPayload getPayload() {
        return payload;
    }
}
