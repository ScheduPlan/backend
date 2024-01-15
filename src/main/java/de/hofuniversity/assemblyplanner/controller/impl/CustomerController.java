package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;
import de.hofuniversity.assemblyplanner.service.api.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(@Autowired CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "gets all customers")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Customer> getCustomers(@ParameterObject @ModelAttribute CustomerQuery query) {
        return customerService.getCustomers(query);
    }

    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "gets a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer getCustomer(@PathVariable UUID customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "creates a customer")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest);
    }

    @PatchMapping("/{customerId}")
    @Operation(summary = "updates a customer. NULL values are ignored", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer patchCustomer(@PathVariable UUID customerId, @RequestBody @Valid CustomerRequest patchRequest) {
        return customerService.patchCustomer(customerId, patchRequest);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "updates a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer putCustomer(@PathVariable UUID customerId, @RequestBody CustomerRequest putRequest) {
        return customerService.putCustomer(customerId, putRequest);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "deletes a customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Customer deleteCustomer(@PathVariable UUID customerId) {
        return customerService.deleteCustomer(customerId);
    }
}
