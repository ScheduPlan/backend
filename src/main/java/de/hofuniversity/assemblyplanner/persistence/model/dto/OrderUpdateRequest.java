package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

public record OrderUpdateRequest(
        Integer number,
        String description,
        Integer commissionNumber,
        Double weight,
        OrderState state
) { }
