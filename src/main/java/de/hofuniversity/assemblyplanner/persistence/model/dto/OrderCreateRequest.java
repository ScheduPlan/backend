package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record OrderCreateRequest (
        Integer number,
        String description,
        @NotBlank String commissionNumber,
        Double plannedDuration,
        Double weight,
        UUID teamId
) { }
