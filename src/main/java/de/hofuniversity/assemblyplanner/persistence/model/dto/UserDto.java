package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;

import java.util.Date;

public record UserDto(
        String username,
        String email,
        Role role,
        boolean locked,
        boolean enabled,
        Date lastPasswordChange
) {
    public UserDto(User user) {
        this(user.getUsername(), user.getEmail(), user.getRole(), !user.isAccountNonLocked(), user.isEnabled(), user.getLastPasswordChange());
    }
}
