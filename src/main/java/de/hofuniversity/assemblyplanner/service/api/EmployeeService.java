package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDefinition;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;

import java.util.UUID;

public interface EmployeeService {
    Employee getEmployee(UUID employeeId);

    Iterable<Employee> getEmployees();

    Employee createEmployee(EmployeeDefinition employeeDefinition);

    Employee patchEmployee(UUID employeeId, EmployeeUpdateRequest patchRequest);

    Employee putEmployee(UUID employeeId, EmployeeUpdateRequest putRequest);

    Employee findHelper(UUID customerId, UUID orderId, UUID eventId, UUID helperId);

    Iterable<Employee> getEmployeesWithRole(Role role);

    void deleteUser(UUID userId);
}
