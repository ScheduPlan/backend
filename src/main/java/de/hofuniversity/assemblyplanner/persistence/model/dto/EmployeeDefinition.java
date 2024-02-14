package de.hofuniversity.assemblyplanner.persistence.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmployeeDefinition(
        PersonRequest person,
        @Valid UserDefinition userDefinition,
        @Min(100000)
        Integer employeeNumber,
        String position,
        UUID teamId

) {
        @JsonCreator
        public EmployeeDefinition(@NotNull String firstName, @NotNull String lastName, @Valid UserDefinition userDefinition, @Min(100000) Integer employeeNumber, String position, UUID teamId){
                this(new PersonRequest(firstName, lastName), userDefinition, employeeNumber, position, teamId);
        }
}
