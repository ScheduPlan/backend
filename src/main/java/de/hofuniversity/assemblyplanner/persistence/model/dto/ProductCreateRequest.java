package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

public record ProductCreateRequest(
        @NonNull
        @NotBlank
        String name,
        String description,
        double materialWidth,
        String materialName,
        String materialGroup,
        String productGroup
) { }
