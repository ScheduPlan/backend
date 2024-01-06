package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Order;

import java.util.UUID;

public record OrderDeleteResponse (
        UUID id,
        Integer number,
        String description,
        Integer commissionNumber,
        Double weight,
        Double plannedDuration
) {
    public OrderDeleteResponse(Order order) {
        this(order.getId(), order.getNumber(), order.getDescription(), order.getCommissionNumber(), order.getWeight(), order.getPlannedDuration());
    }
}
