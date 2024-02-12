package de.hofuniversity.assemblyplanner.web;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeDefinition;
import de.hofuniversity.assemblyplanner.persistence.model.dto.PersonRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.UserDefinition;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeService employeeService;
    private Faker faker = new Faker();


    @Test
    public void shouldReturnOkOnParameterlessRequest() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        mockMvc.perform(get("/employees")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturnOkOnRoleFilterRequest() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        mockMvc.perform(get("/employees")
                .queryParam("role", "ADMINISTRATOR"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturnOkOnUnassignedFilterRequest() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        mockMvc.perform(get("/employees")
                .queryParam("unassigned", "true"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturnOkOnFirstNameFilterRequest() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        mockMvc.perform(get("/employees")
                .queryParam("firstName", "test"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturnOkOnLastNameFilterRequest() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        mockMvc.perform(get("/employees")
                .queryParam("lastName", "test"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturnUnassignedEmployees() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null,admin.getAuthorities()));
        employeeService.createEmployee(new EmployeeDefinition(
                new PersonRequest(faker.name().firstName(), faker.name().lastName()),
                new UserDefinition(
                        faker.internet().emailAddress(),
                        faker.internet().username(),
                        faker.internet().password(),
                        Role.FITTER),
                faker.number().numberBetween(10000, 100000),
                faker.company().profession(),
                null));

        mockMvc.perform(get("/employees")
                .queryParam("unassigned", "true").queryParam("role", "FITTER"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
