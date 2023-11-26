package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;

import java.util.UUID;

public record OrderCreateRequest (
        Integer number,
        String description,
        Integer commissionNumber,
        Double weight
) { }
