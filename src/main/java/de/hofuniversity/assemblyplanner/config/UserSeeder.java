package de.hofuniversity.assemblyplanner.config;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.persistence.model.dto.UserDefinition;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Configuration
public class UserSeeder {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public UserSeeder(EmployeeRepository employeeRepository, UserService userService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    @EventListener
    public void createAdminUser(ContextRefreshedEvent evt) {
        User user = userService.createUser(
                new UserDefinition("admin@localhost", "admin", "admin", Role.ADMINISTRATOR),
                true
        );

        this.employeeRepository.save(
                new Employee("Administrator", "Administrator", user)
        );
    }
}
