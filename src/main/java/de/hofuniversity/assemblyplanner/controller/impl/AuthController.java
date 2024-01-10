package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.persistence.model.dto.*;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.security.model.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(
            @Autowired EmployeeRepository employeeRepository,
            @Autowired UserService userService,
            @Autowired AuthenticationService authenticationService,
            @Autowired TeamRepository teamRepository) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "create a new employee.", description = "creates a new employee. " +
            "If a role is not explicitly defined, FITTER will be used. " +
            "May only be called by ADMINISTRATORS or MANAGERS. " +
            "MANAGERS may only create users with role MANAGER or below.")
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"ADMINISTRATOR", "MANAGER"})
    public Employee register(@RequestBody EmployeeDefinition employeeDefinition) {
        User user = userService.createUser(employeeDefinition.userDefinition());

        AssemblyTeam team = null;
        if(employeeDefinition.teamId() != null) {
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

    @GetMapping("/self")
    @Operation(summary = "returns information about the logged-in user")
    @ResponseStatus(HttpStatus.OK)
    public Employee getSelf() {
        return userService.getCurrentUser();
    }

    @PostMapping("/login")
    @Operation(summary = "log in to the service")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirements
    public LoginResponse login(@RequestBody @Valid LoginInfo loginInfo) {
        AuthenticationDetails details = authenticationService.login(loginInfo.username(), loginInfo.password());

        return new LoginResponse(details.token(), details.refreshToken(), details.employee().getId());
    }

    @PostMapping("/refresh")
    @Operation(summary = "refresh the current access token using a refresh token")
    @ResponseStatus(HttpStatus.OK)
    public RefreshResponse refresh(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthenticationDetails details = authenticationService.newAccessToken(refreshRequest.refreshToken());
        return new RefreshResponse(details.token(), details.employee().getId());
    }

    @PostMapping("/reset")
    @Operation(summary = "change a user's password", description = "changes a user's password." +
            " If only \"password\" is given, changes the password of the logged-in user." +
            " If password and userId is given, the user's password is changed without validating the logged-in user." +
            " This operation may only be performed by administrators or managers. Only users with a role lower than" +
            " the current user's role can be changed.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(@RequestBody PasswordUpdateRequest pwUpdateRequest) {
        if(pwUpdateRequest.userId() == null) {
            userService.changePassword(pwUpdateRequest.password());
            return;
        }

        Employee user = employeeRepository
                .findById(pwUpdateRequest.userId())
                .orElseThrow(ResourceNotFoundException::new);

        userService.changePassword(user, pwUpdateRequest.password());
    }
}
