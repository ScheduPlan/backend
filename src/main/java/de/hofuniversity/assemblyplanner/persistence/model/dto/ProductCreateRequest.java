package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Part;

import java.util.List;

public record ProductCreateRequest(
        String name,
        String description,
        double materialWidth,
        String materialName,
        String materialGroup,
        String productGroup
) { }
