package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record OrderCreateRequest (
        Integer number,
        String description,
        Integer commissionNumber,
        Double weight
) { }
