package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.security.model.TokenDescription;
import de.hofuniversity.assemblyplanner.persistence.model.dto.UserDefinition;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new EmployeeUserDetailsAdapter(loadEmployeeByUsername(username));
    }

    public Employee loadUserByToken(TokenDescription token) {
        if(token.userId() != null) {
            return employeeRepository
                    .findById(token.userId())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        }
        else if(token.subject() != null) {
            return loadEmployeeByUsername(token.subject());
        }

        return null;
    }

    public Employee loadEmployeeByUsername(String username) {
        return employeeRepository
                .findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public User createUser(UserDefinition definition) {
        String hashedPassword = passwordEncoder.encode(definition.password());
        return new User(
                definition.username(),
                definition.email(),
                hashedPassword,
                Role.FITTER
        );
    }

    public User promoteUser(User user, Role role) {
        UserDetails currentUserDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int currentRoleOrdinal = currentUserDetails
                .getAuthorities()
                .stream()
                .map(a -> Role.valueOf(a.getAuthority()).ordinal())
                .max(Comparator.comparingInt(Integer::intValue))
                .orElseThrow();

        if(currentRoleOrdinal <= role.ordinal())
            throw new InsufficientAuthenticationException("users with role " + Role.values()[currentRoleOrdinal]
            + " are not authorized to promote other users to role " + role);

        user.setRole(role);
        return user;
    }
}
