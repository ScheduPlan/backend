package de.hofuniversity.assemblyplanner.persistence.model.dto;

import java.util.UUID;

public record EmployeeDefinition(
        PersonRequest person,
        UserDefinition userDefinition,
        Integer employeeNumber,
        String position,
        UUID teamId

) { }
