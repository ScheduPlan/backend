package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

public record OrderListItem(Integer number, String description, String commissionNumber, Double weight, OrderState orderState) {
    public OrderListItem(Order order) {
        this(order.getNumber(), order.getDescription(), order.getCommissionNumber(), order.getWeight(), order.getState());
    }
}
