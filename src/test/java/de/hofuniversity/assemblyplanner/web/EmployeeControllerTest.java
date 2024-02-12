package de.hofuniversity.assemblyplanner.web;

import de.hofuniversity.assemblyplanner.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


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
}
