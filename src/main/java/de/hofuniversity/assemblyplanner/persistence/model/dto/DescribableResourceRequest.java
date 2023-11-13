package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record DescribableResourceRequest(
        String name,
        String description
) { }
