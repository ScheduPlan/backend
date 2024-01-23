package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.*;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDefinition;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;

    public EmployeeServiceImpl(
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

    @Override
    @GetMapping("/{employeeId}")
    @Operation(summary = "gets an employee", responses = {
            @ApiResponse(responseCode = "404", description = "the employee was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Employee getEmployee(UUID employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Iterable<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee createEmployee(EmployeeDefinition employeeDefinition) {
        User currentUser = userService.getCurrentUser().getUser();
        if(!currentUser.isSuperiorTo(employeeDefinition.userDefinition().role())
                || (employeeDefinition.userDefinition().role() == Role.ADMINISTRATOR && !currentUser.hasRole(Role.ADMINISTRATOR)))
            throw new AccessDeniedException("the current user is not allowed to create new users");

        User user = userService.createUser(employeeDefinition.userDefinition());

        AssemblyTeam team = null;
        if(employeeDefinition.teamId() != null) {
            if(user.hasRole(Role.MANAGER))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");

            team = teamRepository
                    .findById(employeeDefinition.teamId())
                    .orElseThrow(ResourceNotFoundException::new);
        }

        Employee employee = new Employee(
                employeeDefinition.person().firstName(),
                employeeDefinition.person().lastName(),
                employeeDefinition.employeeNumber(),
                employeeDefinition.position(),
                team,
                null,
                user,
                null
        );

        return employeeRepository.save(employee);
    }

    @Override
    public Employee patchEmployee(UUID employeeId, EmployeeUpdateRequest patchRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        Employee currentUser = userService.getCurrentUser();

        if(!(employee.equals(currentUser) || currentUser.getUser().isSuperiorTo(employee.getUser()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to change this user's data");
        }

        if(patchRequest.employeeNumber() != null)
            employee.setEmployeeNumber(patchRequest.employeeNumber());

        if(patchRequest.addressId() != null) {
            Address address = addressRepository.findById(patchRequest.addressId())
                    .orElseThrow(() -> new ResourceNotFoundException("address not found"));
            employee.setAddress(address);
        }

        if(patchRequest.teamId() != null){
            if(employee.getUser().hasRole(Role.MANAGER))
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

    @Override
    public Employee putEmployee(UUID employeeId, EmployeeUpdateRequest putRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        Employee currentUser = userService.getCurrentUser();

        if(!(employee.equals(currentUser) || currentUser.getUser().isSuperiorTo(employee.getUser()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to change this user's data");
        }

        if(putRequest.teamId() != null && employee.getUser().hasRole(Role.MANAGER))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");

        BeanUtils.copyProperties(putRequest, employee, "user", "person");
        BeanUtils.copyProperties(putRequest.user(), employee.getUser());
        BeanUtils.copyProperties(putRequest.person(), employee);
        employeeRepository.save(employee);
        return employee;
    }

    @Override
    public Employee findHelper(UUID customerId, UUID orderId, UUID eventId, UUID helperId) {
        return employeeRepository
                .findEventHelper(eventId, orderId, customerId, helperId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void deleteUser(UUID userId) {
        Employee employee = employeeRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        try {
            userService.deleteUser(employee);
        } catch (InsufficientAuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }
}
