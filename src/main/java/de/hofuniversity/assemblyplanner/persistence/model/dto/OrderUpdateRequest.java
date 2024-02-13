package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

public record OrderUpdateRequest(
        Integer number,
        String description,
        String commissionNumber,
        Double weight,
        OrderState state,
        UUID teamId,
        Double plannedDuration,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Date plannedExecutionDate
) { }
