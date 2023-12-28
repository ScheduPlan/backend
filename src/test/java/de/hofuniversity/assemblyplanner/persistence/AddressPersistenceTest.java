package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.AddressType;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import de.hofuniversity.assemblyplanner.persistence.model.specification.AddressSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AddressPersistenceTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AddressRepository addressRepository;

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
    public void shouldFindAddressesByCustomer() {
        var addresses = addressRepository.getAddressesByCustomer(
                customerRepository.findAll().iterator().next().getId());
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldFindAddressesByCity() {
        var query = new AddressQuery("city", null, null, null, null);
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldFindAddressesByZip() {
        var query = new AddressQuery(null, "23", null, null, null);
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldFindAddressesByCountry() {
        var query = new AddressQuery(null, null, "D", null, null);
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldFindAddressesByDescription() {
        var query = new AddressQuery(null, null, null, "des", null);
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldFindAddressesBySuffix() {
        var query = new AddressQuery(null, null, null, null, "S");
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(1);
    }

    @Test
    public void shouldNotFindAddressOnNonMatchingQuery() {
        var query = new AddressQuery("town", null, null, null, null);
        var addresses = addressRepository.findAll(new AddressSpecification(query));
        assertThat(addresses).hasSize(0);
    }

    @Test
    public void shouldScopeToCustomerIfDefined() {
        var query = new AddressQuery("city", null, null, null, null);
        UUID customerId = customerRepository.findAll().iterator().next().getId();
        var addresses = addressRepository.findAll(new AddressSpecification(query, customerId));
        assertThat(addresses).hasSize(1);
        addresses = addressRepository.findAll(new AddressSpecification(query, UUID.randomUUID()));
        assertThat(addresses).hasSize(0);
    }
}
