package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.persistence.model.dto.*;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.security.model.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    public AuthController(
            @Autowired EmployeeRepository employeeRepository,
            @Autowired UserService userService,
            @Autowired AuthenticationManager authenticationManager,
            @Autowired AuthenticationService authenticationService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/create")
    @Operation(summary = "create a new employee.", description = "creates a new employee. " +
            "If a role is not explicitly defined, FITTER will be used. May only be called by ADMINISTRATORS.")
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"ADMINISTRATOR", "MANAGER"})
    public Employee register(@RequestBody EmployeeDefinition employeeDefinition) {
        User user = userService.createUser(employeeDefinition.userDefinition());
        Employee employee = new Employee(
                employeeDefinition.person().firstName(),
                employeeDefinition.person().lastName(),
                user
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
    public LoginResponse login(@RequestBody LoginInfo loginInfo) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginInfo.username(), loginInfo.password()));

        AuthenticationDetails details = authenticationService.login((UserDetails) auth.getPrincipal(), Map.of());

        return new LoginResponse(details.token(), details.refreshToken(), details.employee().getId());
    }

    @PostMapping("/refresh")
    @Operation(summary = "refresh the current access token using a refresh token")
    @ResponseStatus(HttpStatus.OK)
    public RefreshResponse refresh(@RequestBody RefreshRequest refreshRequest) {
        AuthenticationDetails details = authenticationService.newAccessToken(refreshRequest.refreshToken());
        return new RefreshResponse(details.token(), details.employee().getId());
    }
}
