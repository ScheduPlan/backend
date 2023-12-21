package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;

public record UserDefinition (String email, String username, String password, Role role) {
    public UserDefinition(String email, String username, String password) {
        this(email, username, password, Role.FITTER);
    }
}
