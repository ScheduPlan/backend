package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;
import de.hofuniversity.assemblyplanner.persistence.model.specification.CustomerSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.service.api.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(@Autowired CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Iterable<Customer> getCustomers(CustomerQuery query) {
        return customerRepository.findAll(new CustomerSpecification(query));
    }

    @Override
    public Customer getCustomer(UUID customerId) {
        return customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Customer createCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer(
                customerRequest.company(),
                customerRequest.customerNumber(),
                customerRequest.description(),
                customerRequest.person().firstName(),
                customerRequest.person().lastName(),
                customerRequest.email(),
                customerRequest.phoneNumber());
        LOGGER.info("creating new customer {}", customerRequest.person());
        return customerRepository.save(customer);
    }

    @Override
    public Customer patchCustomer(UUID customerId,  CustomerRequest patchRequest) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        if(patchRequest.customerNumber() > 0)
            customer.setCustomerNumber(patchRequest.customerNumber());
        if(patchRequest.company() != null)
            customer.setCompany(patchRequest.company());
        if(patchRequest.description() != null)
            customer.setDescription(patchRequest.description());
        if(patchRequest.phoneNumber() != null)
            customer.setPhoneNumber(patchRequest.phoneNumber());
        if(patchRequest.email() != null)
            customer.setEmail(patchRequest.email());

        Person.assign(patchRequest.person(), customer, true);
        LOGGER.info("updating customer {} using patch", customerId);
        return customerRepository.save(customer);
    }

    @Override
    public Customer putCustomer(UUID customerId, CustomerRequest putRequest) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putRequest, customer);
        Person.assign(putRequest.person(), customer, false);
        LOGGER.info("updating customer {} using update", customerId);
        return customerRepository.save(customer);
    }

    @Override
    public Customer deleteCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        customerRepository.delete(customer);
        LOGGER.info("deleted customer {}", customerId);
        return customer;
    }
}
