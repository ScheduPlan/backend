package de.hofuniversity.assemblyplanner.persistence.model.dto;

import org.springframework.lang.NonNull;

import java.util.UUID;

public record ProductPartAppendRequest(
        @NonNull UUID partId,
        int amount
) { }
