package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.*;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "gets an employee", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee getEmployee(@PathVariable UUID employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @GetMapping
    @Operation(summary = "gets all employees")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    @PatchMapping("/{employeeId}")
    @Operation(summary = "updates an employee. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee patchEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest patchRequest) {
        return employeeService.patchEmployee(employeeId, patchRequest);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "updates an employee. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee putEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest putRequest) {
        return employeeService.putEmployee(employeeId, putRequest);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "deletes an employee", description = "deletes an employee by ID. Users may be deleted if the following " +
            "conditions are met: The user to delete is the current user OR " +
            "the user to delete has an inferior role to the user making the request OR " +
            "the user making the request has an administrative role.", responses = {
            @ApiResponse(responseCode = "404", description = "the user to delete does not exist"),
            @ApiResponse(responseCode = "403", description = "the user making the request does not have permission" +
                    " to delete the specified user")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        employeeService.deleteUser(userId);
    }
}
