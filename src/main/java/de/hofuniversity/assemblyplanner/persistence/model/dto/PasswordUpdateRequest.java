package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record PasswordUpdateRequest (String password, UUID userId) {

}
