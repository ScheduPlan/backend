package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.*;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDefinition;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.specification.EmployeeSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

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
        LOGGER.debug("retrieving employee {}", employeeId);
        return employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Iterable<Employee> getEmployees() {
        LOGGER.info("retrieving all employees");
        return employeeRepository.findAll();
    }

    @Override
    public Iterable<Employee> queryEmployees(EmployeeQuery query) {
        LOGGER.info("employees are being queried using query {}", query);
        return query == null ? employeeRepository.findAll() : employeeRepository.findAll(new EmployeeSpecification(query));
    }

    @Override
    public Employee createEmployee(EmployeeDefinition employeeDefinition) {
        User currentUser = userService.getCurrentUser().getUser();
        LOGGER.info("creating employee using employee definition {}", employeeDefinition);
        if(!currentUser.isSuperiorTo(employeeDefinition.userDefinition().role())
                || (employeeDefinition.userDefinition().role() == Role.ADMINISTRATOR && !currentUser.hasRole(Role.ADMINISTRATOR))) {
            LOGGER.error("tried to create user {} as {}. Insufficient permissions.", employeeDefinition, currentUser);
            throw new AccessDeniedException("the current user is not allowed to create new users");
        }

        User user = userService.createUser(employeeDefinition.userDefinition());

        AssemblyTeam team = null;
        if(employeeDefinition.teamId() != null) {
            if(user.hasRole(Role.MANAGER)) {
                LOGGER.error("Tried to add {} to a team. Denying request.", employeeDefinition);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");
            }

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

        LOGGER.info("employee created using definition {}", employeeDefinition);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee patchEmployee(UUID employeeId, EmployeeUpdateRequest patchRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        Employee currentUser = userService.getCurrentUser();

        LOGGER.info("updating user {} using patch {}", employeeId, patchRequest);

        if(!(employee.equals(currentUser) || currentUser.getUser().isSuperiorTo(employee.getUser()))) {
            LOGGER.error("current user is not allowed to modify user {}.", employeeId);
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
            if(employee.getUser().hasRole(Role.MANAGER)) {
                LOGGER.error("conflict trying to add user {} of type MANAGER to a team", employeeId);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");
            }

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

        employee = employeeRepository.save(employee);
        LOGGER.info("successfully saved employee {}", employeeId);
        return employee;
    }

    @Override
    public Employee putEmployee(UUID employeeId, EmployeeUpdateRequest putRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(ResourceNotFoundException::new);
        Employee currentUser = userService.getCurrentUser();

        if(!(employee.equals(currentUser) || currentUser.getUser().isSuperiorTo(employee.getUser()))) {
            LOGGER.error("current user is not allowed to modify user {}", employeeId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to change this user's data");
        }

        if(putRequest.teamId() != null) {
            if (employee.getUser().hasRole(Role.MANAGER)) {
                LOGGER.error("conflict trying to add user {} of type MANAGER to a team", employeeId);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "only users with role FITTER may be added to a team.");
            }

            AssemblyTeam team = teamRepository
                    .findById(putRequest.teamId())
                    .orElseThrow(ResourceNotFoundException::new);

            employee.setTeam(team);
        } else {
            employee.setTeam(null);
        }

        LOGGER.info("modifying user {} using update request {}", employeeId, putRequest);

        BeanUtils.copyProperties(putRequest, employee, "user", "person");
        LOGGER.debug("assigned {} to employee {}", putRequest, employeeId);
        BeanUtils.copyProperties(putRequest.user(), employee.getUser());
        LOGGER.debug("assigned {} to employee {}", putRequest.user(), employeeId);
        BeanUtils.copyProperties(putRequest.person(), employee);
        LOGGER.debug("assigned {} to employee {}", putRequest.person(), employeeId);
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
    public Iterable<Employee> getEmployeesWithRole(Role role) {
        return employeeRepository.findByRole(role);
    }

    @Override
    public void deleteUser(UUID userId) {
        LOGGER.info("deleting employee {}", userId);
        Employee employee = employeeRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        try {
            userService.deleteUser(employee);
            LOGGER.info("successfully deleted user {}", userId);
        } catch (InsufficientAuthenticationException ex) {
            LOGGER.error("current user is not allowed to delete user {}, throwing exception.", userId, ex);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }
}
