package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.Valid;

import java.util.UUID;

public record EmployeeUpdateRequest (
        Integer employeeNumber,
        String position,
        UUID teamId,
        UUID addressId,
        @Valid PersonRequest person,
        @Valid UserUpdateRequest user
) { }
