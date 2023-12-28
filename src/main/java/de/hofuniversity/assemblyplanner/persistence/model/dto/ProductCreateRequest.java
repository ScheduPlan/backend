package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

import java.util.List;

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
