package de.hofuniversity.assemblyplanner.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hofuniversity.assemblyplanner.persistence.model.EventType;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.PersonRequest;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.service.api.CustomerOrderService;
import de.hofuniversity.assemblyplanner.service.api.CustomerService;
import de.hofuniversity.assemblyplanner.service.api.OrderEventService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    private MockMvc mock;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerOrderService orderService;
    @Autowired
    private OrderEventService eventService;
    @Autowired
    private UserService userService;
    private Faker faker = new Faker();

    @Test
    public void shouldDeleteEvents() throws Exception {
        UserDetails admin = userService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
        var customer = customerService.createCustomer(new CustomerRequest(
                faker.company().name(),
                faker.number().positive(),
                faker.text().text(),
                new PersonRequest(faker.name().firstName(), faker.name().lastName()),
                faker.phoneNumber().phoneNumberNational(),
                faker.internet().emailAddress()
        ));
        var order = orderService.createOrder(customer.getId(), new OrderCreateRequest(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("##ABC####"),
                faker.number().randomDouble(1, 0, 10),
                faker.number().randomDouble(3, 1, 10),
                null
        ));
        var event = eventService.createEvent(customer.getId(), order.getId(), new EventCreateRequest(
                new Date(), new Date(Instant.now().plus(Duration.ofHours(5)).toEpochMilli()), EventType.ASSEMBLY, "Montage", faker.text().text()
        ));

        mock.perform(delete("/customers/{customerId}/orders/{orderId}/events/{eventId}", customer.getId(), order.getId(), event.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(hasLength(0)));

    }
}
