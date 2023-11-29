package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record EmployeeDefinition(
        PersonRequest person,
        UserDefinition userDefinition
) { }
