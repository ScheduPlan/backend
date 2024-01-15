package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;
import de.hofuniversity.assemblyplanner.persistence.model.specification.CustomerSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.service.api.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

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

        return customerRepository.save(customer);
    }

    @Override
    public Customer putCustomer(UUID customerId, CustomerRequest putRequest) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putRequest, customer);
        Person.assign(putRequest.person(), customer, false);
        return customerRepository.save(customer);
    }

    @Override
    public Customer deleteCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        customerRepository.delete(customer);
        return customer;
    }
}
