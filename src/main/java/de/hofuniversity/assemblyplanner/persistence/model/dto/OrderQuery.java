package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

public record OrderQuery(
        Integer number,
        String commissionNumber,
        OrderState[] states,
        String description
)
{ }
