package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Event;

import java.util.UUID;

public record EmployeeDto (
        UUID id,
        String firstName,
        String lastName,
        Integer employeeNumber,
        String position,
        UUID teamId,
        UserDto user,
        Iterable<Event> helpsOn
) {
    public EmployeeDto(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmployeeNumber(),
                employee.getPosition(), employee.getTeam() == null ? null : employee.getTeam().getId(), new UserDto(employee.getUser()), employee.getHelpsOn());
    }
}
