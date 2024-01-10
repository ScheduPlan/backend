package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.Valid;

import java.util.UUID;

public record EmployeeDefinition(
        @Valid PersonRequest person,
        @Valid UserDefinition userDefinition,
        Integer employeeNumber,
        String position,
        UUID teamId

) { }
