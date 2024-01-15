package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDefinition;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface EmployeeService {
    Employee getEmployee(UUID employeeId);

    Iterable<Employee> getEmployees();

    Employee createEmployee(EmployeeDefinition employeeDefinition);

    Employee patchEmployee(UUID employeeId, EmployeeUpdateRequest patchRequest);

    Employee putEmployee(UUID employeeId, EmployeeUpdateRequest putRequest);

    Employee findHelper(UUID customerId, UUID orderId, UUID eventId, UUID helperId);

    void deleteUser(UUID userId);
}
