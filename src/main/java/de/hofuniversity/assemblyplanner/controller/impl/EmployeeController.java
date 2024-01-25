package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDto;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    public EmployeeDto getEmployee(@PathVariable UUID employeeId) {
        return new EmployeeDto(employeeService.getEmployee(employeeId));
    }

    @GetMapping
    @Operation(summary = "gets all employees")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<EmployeeListItem> getEmployees(@RequestParam(required = false) Role role) {
        List<EmployeeListItem> items = new ArrayList<>();
        Iterable<Employee> employees = role == null ? employeeService.getEmployees() : employeeService.getEmployeesWithRole(role);
        employees.forEach(e -> items.add(new EmployeeListItem(e)));
        return items;
    }

    @PatchMapping("/{employeeId}")
    @Operation(summary = "updates an employee. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto patchEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest patchRequest) {
        return new EmployeeDto(employeeService.patchEmployee(employeeId, patchRequest));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "updates an employee.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto putEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest putRequest) {
        return new EmployeeDto(employeeService.putEmployee(employeeId, putRequest));
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
