package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResourceRequest(@NotNull UUID resourceId) {
}
