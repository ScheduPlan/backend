package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(@Autowired CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    @Operation(summary = "gets all customers")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "gets a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer getCustomer(@PathVariable UUID customerId) {
        return customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "creates a customer")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody CustomerRequest customerRequest) {
        Customer customer = new Customer(
                customerRequest.company(),
                customerRequest.customerNumber(),
                customerRequest.description(),
                customerRequest.person().firstName(),
                customerRequest.person().lastName());
        return customerRepository.save(customer);
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "updates a customer. NULL values are ignored", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer patchCustomer(@PathVariable UUID customerId, @RequestBody CustomerRequest patchRequest) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        if(patchRequest.customerNumber() > 0)
            customer.setCustomerNumber(patchRequest.customerNumber());
        if(patchRequest.company() != null)
            customer.setCompany(patchRequest.company());
        if(patchRequest.description() != null)
            customer.setDescription(patchRequest.description());
        Person.assign(patchRequest.person(), customer, true);

        return customerRepository.save(customer);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "updates a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer putCustomer(@PathVariable UUID customerId, @RequestBody CustomerRequest putRequest) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putRequest, customer);
        Person.assign(putRequest.person(), customer, false);
        return customerRepository.save(customer);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "deletes a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer deleteCustomer(@PathVariable UUID customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        customerRepository.delete(customer);
        return customer;
    }
}
