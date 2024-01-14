package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.*;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;

    public EmployeeController(
            EmployeeRepository employeeRepository,
            UserService userService,
            AddressRepository addressRepository,
            TeamRepository teamRepository
    ){
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.addressRepository = addressRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "gets an employee", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee getEmployee(@PathVariable UUID employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping
    @Operation(summary = "gets all employees")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @PatchMapping("/{employeeId}")
    @Operation(summary = "updates an employee. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee patchEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest patchRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);

        if(patchRequest.employeeNumber() != null)
            employee.setEmployeeNumber(patchRequest.employeeNumber());

        if(patchRequest.addressId() != null) {
            Address address = addressRepository.findById(patchRequest.addressId())
                    .orElseThrow(() -> new ResourceNotFoundException("address not found"));
            employee.setAddress(address);
        }

        if(patchRequest.teamId() != null){
            if(userService.getCurrentUser().getUser().hasRole(Role.MANAGER))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");

            AssemblyTeam team = teamRepository.findById(patchRequest.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("team not found"));
            employee.setTeam(team);
        }

        if(patchRequest.position() != null)
            employee.setPosition(patchRequest.position());

        if(patchRequest.user() != null) {
            if(patchRequest.user().role() != null) {
                userService.promoteUser(employee.getUser(), patchRequest.user().role());
            }
            if(patchRequest.user().email() != null) {
                employee.getUser().setEmail(patchRequest.user().email());
            }
            if(patchRequest.user().username() != null) {
                userService.updateName(employee.getUser(), patchRequest.user().username());
            }
        }

        if(patchRequest.person() != null) {
            Person.assign(patchRequest.person(), employee, true);
        }

        employeeRepository.save(employee);
        return employee;
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "updates an employee. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee putEmployee(@PathVariable UUID employeeId, @RequestBody @Valid EmployeeUpdateRequest putRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        if(putRequest.teamId() != null && userService.getCurrentUser().getUser().hasRole(Role.MANAGER))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");

        BeanUtils.copyProperties(putRequest, employee, "user", "person");
        BeanUtils.copyProperties(putRequest.user(), employee.getUser());
        BeanUtils.copyProperties(putRequest.person(), employee);
        employeeRepository.save(employee);
        return employee;
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
        Employee employee = employeeRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        try {
            userService.deleteUser(employee);
        } catch (InsufficientAuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }
}
