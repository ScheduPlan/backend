package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record OrderCreateRequest (
        Integer number,
        String description,
        Integer commissionNumber,
        Double weight,
        UUID teamId
) { }
