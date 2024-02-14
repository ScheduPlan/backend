package de.hofuniversity.assemblyplanner.persistence.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmployeeUpdateRequest (
        @Min(100000)
        Integer employeeNumber,
        String position,
        UUID teamId,
        UUID addressId,
        @Valid PersonRequest person,
        @Valid UserUpdateRequest user
) {
        @JsonCreator
        public EmployeeUpdateRequest(@NotNull String firstName, @NotNull String lastName, @Valid UserUpdateRequest userUpdateRequest, @Min(100000) Integer employeeNumber, String position, UUID teamId, UUID addressId){
                this(employeeNumber, position, teamId, addressId, new PersonRequest(firstName, lastName), userUpdateRequest);
        }
}
