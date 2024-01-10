package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;

import java.util.UUID;

public record Helper(
        UUID id,
        String firstName,
        String lastName,
        Integer employeeNumber,
        String position
) {
    public Helper(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmployeeNumber(), employee.getPosition());
    }
}
