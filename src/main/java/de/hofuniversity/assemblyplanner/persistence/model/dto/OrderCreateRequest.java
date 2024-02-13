package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

public record OrderCreateRequest (
        Integer number,
        String description,
        @NotBlank String commissionNumber,
        Double plannedDuration,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Date plannedExecutionDate,
        Double weight,
        UUID teamId
) { }
