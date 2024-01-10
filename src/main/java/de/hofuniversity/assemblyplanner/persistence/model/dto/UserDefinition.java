package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserDefinition (
        @NotEmpty
        @Email
        String email,
        @NotEmpty
        String username,
        @NotEmpty
        String password,
        Role role
) {
    public UserDefinition(String email, String username, String password) {
        this(email, username, password, Role.FITTER);
    }
}
