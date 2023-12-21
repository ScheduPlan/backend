package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record EmployeeUpdateRequest (
        Integer employeeNumber,
        String position,
        UUID teamId,
        UUID addressId,
        PersonRequest person,
        UserUpdateRequest user
) { }
