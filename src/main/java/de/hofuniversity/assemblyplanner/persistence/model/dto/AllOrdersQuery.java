package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

import java.util.UUID;

public record AllOrdersQuery(
        String commissionNumber,
        Integer orderNumber,
        String companyName,
        String customerNumber,
        UUID customerId,
        OrderState[] states,
        String description
) { }
