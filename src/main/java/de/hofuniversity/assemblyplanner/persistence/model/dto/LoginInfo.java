package de.hofuniversity.assemblyplanner.persistence.model.dto;

import org.springframework.lang.NonNull;

public record LoginInfo(
        @NonNull
        String username,
        @NonNull
        String password
) { }
