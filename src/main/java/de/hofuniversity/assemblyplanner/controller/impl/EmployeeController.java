package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Employee patchEmployee(@PathVariable UUID employeeId, @RequestBody EmployeeUpdateRequest patchRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);

        if(patchRequest.employeeNumber() != null)
            employee.setEmployeeNumber(patchRequest.employeeNumber());

        if(patchRequest.addressId() != null) {
            Address address = addressRepository.findById(patchRequest.addressId())
                    .orElseThrow(() -> new ResourceNotFoundException("address not found"));
            employee.setAddress(address);
        }

        if(patchRequest.teamId() != null){
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
    public Employee putEmployee(@PathVariable UUID employeeId, @RequestBody EmployeeUpdateRequest putRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putRequest, employee, "user", "person");
        BeanUtils.copyProperties(putRequest.user(), employee.getUser());
        BeanUtils.copyProperties(putRequest.person(), employee);
        employeeRepository.save(employee);
        return employee;
    }
}