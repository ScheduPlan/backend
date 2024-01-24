package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record EmployeeDefinition(
        @Valid PersonRequest person,
        @Valid UserDefinition userDefinition,
        @Min(100000)
        Integer employeeNumber,
        String position,
        UUID teamId

) { }
