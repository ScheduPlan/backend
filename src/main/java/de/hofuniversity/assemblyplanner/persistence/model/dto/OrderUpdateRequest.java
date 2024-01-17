package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

import java.util.UUID;

public record OrderUpdateRequest(
        Integer number,
        String description,
        String commissionNumber,
        Double weight,
        OrderState state,
        UUID teamId,
        Double plannedDuration
) { }
