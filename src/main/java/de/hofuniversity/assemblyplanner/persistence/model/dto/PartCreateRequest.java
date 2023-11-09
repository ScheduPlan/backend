package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record PartCreateRequest(
        String name,
        String description
) { }
