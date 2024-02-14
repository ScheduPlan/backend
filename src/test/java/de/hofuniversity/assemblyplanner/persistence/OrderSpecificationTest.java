package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AllOrdersQuery;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.TeamDescription;
import de.hofuniversity.assemblyplanner.persistence.model.specification.AllOrdersSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import net.datafaker.Faker;
import net.datafaker.providers.base.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderSpecificationTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TestEntityManager entityManager;

    private final Faker faker = new Faker();

    @BeforeEach
    public void setup() {
        Customer customer = new Customer(
                faker.company().name(),
                faker.number().positive(),
                faker.text().text(),
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber()
        );

        AssemblyTeam team = new AssemblyTeam(new TeamDescription(faker.team().name(), faker.text().text()), List.of(), List.of());
        teamRepository.save(team);

        JpaUtil.reset(entityManager);
        team = teamRepository.findById(team.getId()).orElseThrow();

        customer = customerRepository.save(customer);

        orderRepository.save(new Order(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("B##########"),
                3.0,
                OrderState.PLANNED,
                customer,
                Set.of(),
                team,
                3.0,
                null
        ));

        orderRepository.save(new Order(
                faker.number().positive(),
                faker.text().text(),
                faker.numerify("A##########"),
                4.0,
                OrderState.PLANNED,
                customer,
                Set.of(),
                team,
                3.0,
                null
        ));

        JpaUtil.reset(entityManager);
    }

    @AfterEach
    public void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    public void shouldOrderByCompany() {
        List<Order> orders = orderRepository.findAll(new AllOrdersSpecification(
                new AllOrdersQuery(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ), Sort.by("customer_company"));
        assertThat(orders.stream().map(order -> order.getCustomer().getCompany()).toList()).isSorted();
    }

    @Test
    public void shouldFindByCommissionNumber() {
        List<Order> orders = orderRepository.findAll(new AllOrdersSpecification(
                new AllOrdersQuery(
                        "A",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ));
        assertThat(orders).size().isEqualTo(1);
    }

    @Test
    public void shouldFindByOrderNumber() {
        List<Order> orders = orderRepository.findAll(new AllOrdersSpecification(
                new AllOrdersQuery(
                        null,
                        orderRepository.findAll().iterator().next().getNumber(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ));
        assertThat(orders).size().isEqualTo(1);
    }

    @Test
    public void shouldFindByOrderByTeam() {
        List<Order> orders = orderRepository.findAll(new AllOrdersSpecification(
                new AllOrdersQuery(
                        null,
                    null,
                        null,
                        null,
                        null,
                        teamRepository.findAll().iterator().next().getId(),
                        null,
                        null,
                        null
                )
        ));
        assertThat(orders).size().isEqualTo(2);
    }

    @Test
    public void shouldFindByOrderById() {
        List<Order> orders = orderRepository.findAll(new AllOrdersSpecification(
                new AllOrdersQuery(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        orderRepository.findAll().iterator().next().getId(),
                        null,
                        null
                )
        ));
        assertThat(orders).size().isEqualTo(1);
    }
}
