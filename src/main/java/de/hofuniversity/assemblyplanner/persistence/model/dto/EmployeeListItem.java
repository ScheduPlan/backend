package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;

import java.util.UUID;

public record EmployeeListItem (
        String firstName,
        String lastName,
        UUID id
) {
    public EmployeeListItem(Employee employee) {
        this(employee.getFirstName(), employee.getLastName(), employee.getId());
    }
}
