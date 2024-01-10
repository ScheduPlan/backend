package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.Valid;

public record CustomerRequest(
        String company,
        int customerNumber,
        String description,
        @Valid PersonRequest person,
        String phoneNumber,
        String email
) { }
