package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Role;

public record UserUpdateRequest(Role role, String email, String username) {
}
