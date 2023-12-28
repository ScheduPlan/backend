package de.hofuniversity.assemblyplanner.persistence.model.dto;

import org.springframework.lang.NonNull;

public record PersonRequest (
        @NonNull String firstName,
        @NonNull String lastName
) { }
