package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record LoginResponse(String token, UUID userId) {
}
