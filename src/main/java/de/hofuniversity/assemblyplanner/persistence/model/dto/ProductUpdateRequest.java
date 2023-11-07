package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record ProductUpdateRequest (
        String name,
        String description,
        double materialWidth,
        String materialName,
        String materialGroup,
        String productGroup
) { }
