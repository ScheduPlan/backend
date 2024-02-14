package de.hofuniversity.assemblyplanner.persistence.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record EmployeeUpdateRequest (
        @Min(100000)
        Integer employeeNumber,
        String position,
        UUID teamId,
        UUID addressId,
        @Valid @JsonUnwrapped PersonRequest person,
        @Valid UserUpdateRequest user
) { }
