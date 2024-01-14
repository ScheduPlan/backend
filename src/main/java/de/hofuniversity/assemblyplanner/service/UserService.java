package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.persistence.model.dto.UserDefinition;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.security.model.TokenDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new EmployeeUserDetailsAdapter(loadEmployeeByUsername(username));
    }

    public Employee loadUserByToken(TokenDescription token) {
        Employee employee = null;
        if(token.userId() != null) {
            employee = employeeRepository
                    .findById(token.userId())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        }
        else if(token.subject() != null) {
            employee = loadEmployeeByUsername(token.subject());
        }

        return employee;
    }

    public User updateName(User user, String username) {
        if(!getCurrentUser().getUser().hasRole(Role.MANAGER))
            throw new AccessDeniedException("only MANAGERS and ADMINISTRATORS may change user names");

        user.setUsername(username);
        return user;
    }

    public Employee loadEmployeeByUsername(String username) {
        return employeeRepository
                .findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public User createUser(UserDefinition definition, boolean skipValidation) {
        String hashedPassword = passwordEncoder.encode(definition.password());
        User user = new User(
                definition.username(),
                definition.email(),
                hashedPassword,
                Role.FITTER
        );

        if(definition.role() != Role.FITTER && !skipValidation)
            promoteUser(user, definition.role());
        else
            user.setRole(definition.role());

        return user;
    }

    public User createUser(UserDefinition definition) {
        return createUser(definition, false);
    }

    public Employee getCurrentUser() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(auth == null)
            return null;
        return ((EmployeeUserDetailsAdapter) auth).getEmployee();
    }

    public static String sanitizeRoleString(String roleStr) {
        if(!roleStr.startsWith(User.ROLE_PREFIX))
            return roleStr;
        return roleStr.substring(User.ROLE_PREFIX.length());
    }

    public void promoteUser(User user, Role role) {
        Employee currentUser = getCurrentUser();

        if(currentUser == null || !currentUser.getUser().hasRole(role))
            throw new InsufficientAuthenticationException("users with role " + currentUser.getUser().getRole()
            + " are not authorized to promote other users to role " + role);

        user.setRole(role);
    }

    private void savePassword(Employee user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.getUser().setPassword(encodedPassword);
        employeeRepository.save(user);
    }

    public void changePassword(String newPassword) {
        Employee currentUser = getCurrentUser();
        if(currentUser == null)
            throw new InsufficientAuthenticationException("user not authenticated");

        savePassword(currentUser, newPassword);
    }

    public void changePassword(Employee user, String newPassword) {
        User current = getCurrentUser().getUser();
        if(!current.hasRole(Role.MANAGER) || !user.getUser().isInferiorTo(current))
            throw new InsufficientAuthenticationException("users with role " + current.getRole() +
                    " are not allowed to change passwords for users with role " + user.getUser().getRole());

        savePassword(user, newPassword);
    }

    public void deleteUser(Employee user) {
        Employee current = getCurrentUser();
        if(!current.equals(user) && !current.getUser().isSuperiorTo(user.getUser()) && !current.getUser().hasRole(Role.ADMINISTRATOR)) {
            throw new InsufficientAuthenticationException("Permission denied");
        }

        employeeRepository.delete(user);
    }
}
