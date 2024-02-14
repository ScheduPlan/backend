package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.EventType;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import de.hofuniversity.assemblyplanner.persistence.model.dto.*;
import de.hofuniversity.assemblyplanner.service.EmployeeUserDetailsAdapter;
import de.hofuniversity.assemblyplanner.service.api.*;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import net.datafaker.Faker;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
public class EventHelperServiceTest {
    @Autowired
    private OrderEventService eventService;
    @Autowired
    private EventHelperService eventHelperService;
    @Autowired
    private CustomerOrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private TeamService teamService;
    private final Faker faker = new Faker();

    private Customer customer;

    @Test
    @Transactional
    public void shouldAllowCreationOnNoOverlap() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employeeService.getEmployeesWithRole(Role.ADMINISTRATOR).iterator().next()), ""));

        Employee employee = employeeService.createEmployee(new EmployeeDefinition(
                faker.name().firstName(),
                faker.name().lastName(),
                new UserDefinition(
                        faker.internet().emailAddress(),
                        faker.internet().username(),
                        faker.internet().password(),
                        Role.FITTER
                ),
                faker.number().numberBetween(100000, 200000),
                faker.company().profession(),
                null
        ));

        var team = teamService.createTeam(new DescribableResourceRequest(
                faker.team().name(),
                faker.text().text()
        ));

        var anotherTeam = teamService.createTeam(new DescribableResourceRequest(
                faker.team().name(),
                faker.text().text()
        ));

        var customer = customerService.createCustomer(
                new CustomerRequest(
                        faker.company().name(),
                        faker.number().positive(),
                        faker.text().text(),
                        new PersonRequest(faker.name().firstName(), faker.name().lastName()),
                        faker.phoneNumber().phoneNumberNational(),
                        faker.internet().emailAddress()
                )
        );

        var order = orderService.createOrder(customer.getId(), new OrderCreateRequest(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("###ABC######"),
                faker.number().randomDouble(1,1, 3),
                new Date(),
                faker.number().randomDouble(2, 2, 3),
                team.getId()
        ));

        var anotherOrder = orderService.createOrder(customer.getId(), new OrderCreateRequest(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("###ABC######"),
                faker.number().randomDouble(1,1, 3),
                new Date(),
                faker.number().randomDouble(2, 2, 3),
                anotherTeam.getId()
        ));

        var firstEvent = eventService.createEvent(customer.getId(), order.getId(), new EventCreateRequest(
                new Date(),
                new Date(Instant.now().plus(Duration.ofHours(3)).toEpochMilli()),
                EventType.ASSEMBLY,
                "test",
                faker.text().text()
        ));

        eventService.createEvent(customer.getId(), anotherOrder.getId(), new EventCreateRequest(
                new Date(),
                new Date(Instant.now().plus(Duration.ofHours(3)).toEpochMilli()),
                EventType.ASSEMBLY,
                "test",
                faker.text().text()
        ));

        JpaUtil.reset(testEntityManager);

        var fEvent = eventService.getEvent(customer.getId(), order.getId(), firstEvent.getId());

        assertThatCode(() -> eventHelperService.addHelper(customer.getId(), order.getId(), fEvent.getId(), new ResourceRequest(employee.getId())))
                .doesNotThrowAnyException();
    }

    @Test
    @Transactional
    public void shouldDenyCreationOnSameHelper() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new EmployeeUserDetailsAdapter(employeeService.getEmployeesWithRole(Role.ADMINISTRATOR).iterator().next()), ""));

        Employee employee = employeeService.createEmployee(new EmployeeDefinition(
                faker.name().firstName(),
                faker.name().lastName(),
                new UserDefinition(
                        faker.internet().emailAddress(),
                        faker.internet().username(),
                        faker.internet().password(),
                        Role.FITTER
                ),
                faker.number().numberBetween(100000, 200000),
                faker.company().profession(),
                null
        ));

        var team = teamService.createTeam(new DescribableResourceRequest(
                faker.team().name(),
                faker.text().text()
        ));

        var anotherTeam = teamService.createTeam(new DescribableResourceRequest(
                faker.team().name(),
                faker.text().text()
        ));

        var customer = customerService.createCustomer(
                new CustomerRequest(
                        faker.company().name(),
                        faker.number().positive(),
                        faker.text().text(),
                        new PersonRequest(faker.name().firstName(), faker.name().lastName()),
                        faker.phoneNumber().phoneNumberNational(),
                        faker.internet().emailAddress()
                )
        );

        var order = orderService.createOrder(customer.getId(), new OrderCreateRequest(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("###ABC######"),
                faker.number().randomDouble(1,1, 3),
                new Date(),
                faker.number().randomDouble(2, 2, 3),
                team.getId()
        ));

        var anotherOrder = orderService.createOrder(customer.getId(), new OrderCreateRequest(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("###ABC######"),
                faker.number().randomDouble(1,1, 3),
                new Date(),
                faker.number().randomDouble(2, 2, 3),
                anotherTeam.getId()
        ));

        var firstEvent = eventService.createEvent(customer.getId(), order.getId(), new EventCreateRequest(
                new Date(),
                new Date(Instant.now().plus(Duration.ofHours(3)).toEpochMilli()),
                EventType.ASSEMBLY,
                "test",
                faker.text().text()
        ));

        var overlappingEvent = eventService.createEvent(customer.getId(), anotherOrder.getId(), new EventCreateRequest(
                new Date(),
                new Date(Instant.now().plus(Duration.ofHours(3)).toEpochMilli()),
                EventType.ASSEMBLY,
                "test",
                faker.text().text()
        ));


        JpaUtil.reset(testEntityManager);
        eventHelperService.addHelper(customer.getId(), anotherOrder.getId(), overlappingEvent.getId(), new ResourceRequest(employee.getId()));
        JpaUtil.reset(testEntityManager);

        var fEvent = eventService.getEvent(customer.getId(), order.getId(), firstEvent.getId());

        assertThatThrownBy(() -> eventHelperService.addHelper(customer.getId(), order.getId(), fEvent.getId(), new ResourceRequest(employee.getId())))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
