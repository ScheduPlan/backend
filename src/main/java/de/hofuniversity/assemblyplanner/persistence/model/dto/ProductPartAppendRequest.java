package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record ProductPartAppendRequest(UUID partId, int amount) {
}
