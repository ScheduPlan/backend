package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.User;
import de.hofuniversity.assemblyplanner.persistence.model.dto.UserDefinition;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.service.EmployeeUserDetailsAdapter;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class UserPersistenceTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    private UserService userService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    public UserPersistenceTest(EmployeeRepository employeeRepository, TestEntityManager em) {
        this.employeeRepository = employeeRepository;
        this.em = em;
        this.userService = new UserService(employeeRepository, new BCryptPasswordEncoder());
    }

    @Test
    @Transactional
    public void shouldDeleteIfUserIsCurrentUser() {
        var def = new UserDefinition("test@test.de", "test", "test", Role.FITTER);

        User user = userService.createUser(def);
        Employee employee = new Employee("admin", "admin", 1, null, null, null, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employee), ""));

        Employee savedEmployee = employeeRepository.save(employee);
        assertThatCode(() -> userService.deleteUser(savedEmployee)).doesNotThrowAnyException();
        JpaUtil.reset(em);

        assertThat(employeeRepository.count()).isZero();
    }

    @Test
    @Transactional
    public void shouldDeleteIfCurrentUserIsSuperior() {
        var userToDelete = new UserDefinition("test@test.de", "test", "test", Role.FITTER);
        var currentUser = new UserDefinition("manager@test.de", "manager", "manager", Role.MANAGER);

        User user = userService.createUser(currentUser, true);
        Employee employee = new Employee("manager", "manager", 1, null, null, null, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employee), ""));

        user = userService.createUser(userToDelete);
        employee = new Employee("user", "user", 2, null, null, null, user, Set.of());
        Employee savedEmployee = employeeRepository.save(employee);

        assertThatCode(() -> userService.deleteUser(savedEmployee)).doesNotThrowAnyException();
        JpaUtil.reset(em);

        assertThat(employeeRepository.count()).isZero();
    }

    @Test
    @Transactional
    public void shouldDeleteIfCurrentUserIsAdministrator() {
        var userToDelete = new UserDefinition("admin2@test.de", "admin2", "admin", Role.ADMINISTRATOR);
        var currentUser = new UserDefinition("admin1@test.de", "admin1", "admin", Role.ADMINISTRATOR);

        User user = userService.createUser(currentUser, true);
        Employee employee = new Employee("admin", "admin", 1, null, null, null, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employee), ""));

        user = userService.createUser(userToDelete);
        employee = new Employee("admin", "admin", 2, null, null, null, user, Set.of());
        Employee savedEmployee = employeeRepository.save(employee);

        assertThatCode(() -> userService.deleteUser(savedEmployee)).doesNotThrowAnyException();
        JpaUtil.reset(em);

        assertThat(employeeRepository.count()).isZero();
    }

    @Test
    @Transactional
    public void shouldNotDeleteIfCurrentUserIsEqual() {
        var userToDelete = new UserDefinition("user2@test.de", "user2", "user", Role.MANAGER);
        var currentUser = new UserDefinition("user1@test.de", "user1", "user", Role.MANAGER);

        User user = userService.createUser(currentUser, true);
        Employee employee = new Employee("user", "user", 1, null, null, null, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employee), ""));

        user = userService.createUser(userToDelete);
        employee = new Employee("user", "user", 2, null, null, null, user, Set.of());
        Employee savedEmployee = employeeRepository.save(employee);

        assertThatThrownBy(() -> userService.deleteUser(savedEmployee))
                .isInstanceOf(InsufficientAuthenticationException.class)
                .hasMessage("Permission denied");

        JpaUtil.reset(em);

        assertThat(employeeRepository.count()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void shouldNotDeleteIfCurrentUserIsInferior() {
        var userToDelete = new UserDefinition("user2@test.de", "user2", "user", Role.MANAGER);
        var currentUser = new UserDefinition("user1@test.de", "user1", "user", Role.FITTER);

        User user = userService.createUser(currentUser, true);
        Employee employee = new Employee("user", "user", 1, null, null, null, user, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employee), ""));

        user = userService.createUser(userToDelete, true);
        employee = new Employee("user", "user", 2, null, null, null, user, Set.of());
        Employee savedEmployee = employeeRepository.save(employee);

        assertThatThrownBy(() -> userService.deleteUser(savedEmployee))
                .isInstanceOf(InsufficientAuthenticationException.class)
                .hasMessage("Permission denied");

        JpaUtil.reset(em);

        assertThat(employeeRepository.count()).isEqualTo(1);
    }
}
