package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.AddressType;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import de.hofuniversity.assemblyplanner.persistence.model.specification.CustomerSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerPersistenceTest {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        Customer customer = new Customer(
                "company",
                123,
                "description",
                "firstname",
                "lastname"
        );

        customer = customerRepository.save(customer);
        JpaUtil.reset(entityManager);
        customer = customerRepository.findById(customer.getId()).orElseThrow();

        customer.getAddresses().add(
                new Address(
                        "DE",
                        "street",
                        123,
                        "city",
                        "12345",
                        "description",
                        "S",
                        AddressType.DELIVERY
                )
        );

        customerRepository.save(customer);
    }

    @AfterEach
    public void tearDown() {
        customerRepository.deleteAll();
        JpaUtil.reset(entityManager);
    }

    @Test
    public void shouldSearchCustomersByFirstName() {
        CustomerQuery query = new CustomerQuery("first", null, null, null, null);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldSearchCustomersByLastName() {
        CustomerQuery query = new CustomerQuery(null, "last", null, null, null);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldSearchCustomersByCompany() {
        CustomerQuery query = new CustomerQuery(null, null, "comp", null, null);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldSearchCustomersByCustomerNumber() {
        CustomerQuery query = new CustomerQuery(null, null, null, 12, null);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldSearchCustomersByAddress() {
        AddressQuery addressQuery = new AddressQuery("city", null, null, null, null);
        CustomerQuery query = new CustomerQuery(null, null, null, null, addressQuery);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldSearchByMultipleFieldsByAnd() {
        AddressQuery addressQuery = new AddressQuery("city", "12345", null, null, null);
        CustomerQuery query = new CustomerQuery("first", "last", "comp", 12, addressQuery);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(1);
    }

    @Test
    public void shouldNotFindOnNonMatchingQuery() {
        CustomerQuery query = new CustomerQuery("last", null, null, null, null);
        assertThat(customerRepository.findAll(new CustomerSpecification(query))).hasSize(0);
    }
}
