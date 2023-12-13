package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record RefreshResponse (String accessToken, UUID userId) {
}
