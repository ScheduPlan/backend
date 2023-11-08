package de.hofuniversity.assemblyplanner.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/addresses")
public class AddressController {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressController(@Autowired AddressRepository addressRepository, @Autowired CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves all addresses for a given customer")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Address> getAddresses(@PathVariable UUID customerId) {
        return addressRepository.getAddressesByCustomer(customerId);
    }

    @GetMapping(value = "/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves a single address for the given customer", responses = {
            @ApiResponse(responseCode = "404", description = "the given customer or address wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address getAddress(@PathVariable UUID customerId, @PathVariable UUID addressId) {
        return addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "creates a new address for this customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer wasn't found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Address createAddress(@PathVariable UUID customerId, @RequestBody AddressCreateRequest createRequest) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        Address address = new Address(
                createRequest.country(),
                createRequest.street(),
                createRequest.streetNumber(),
                createRequest.city(),
                createRequest.zip(),
                createRequest.description(),
                createRequest.addressSuffix(),
                createRequest.addressType()
        );

        customer.getAddresses().add(address);
        customerRepository.save(customer);
        return address;
    }

    @PatchMapping(value = "/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "patches an address. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address patchAddress(@PathVariable UUID customerId, @PathVariable UUID addressId, @RequestBody AddressCreateRequest createRequest) {
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        if(createRequest.country() != null)
            address.setCountry(createRequest.country());
        if(createRequest.city() != null)
            address.setCity(createRequest.city());
        if(createRequest.addressSuffix() != null)
            address.setAddressSuffix(createRequest.addressSuffix());
        if(createRequest.addressType() != null)
            address.setAddressType(createRequest.addressType());
        if(createRequest.zip() != null)
            address.setZip(createRequest.zip());
        if(createRequest.street() != null)
            address.setStreet(createRequest.street());
        if(createRequest.streetNumber() != 0)
            address.setStreetNumber(createRequest.streetNumber());
        if(createRequest.description() != null)
            address.setDescription(createRequest.description());

        addressRepository.save(address);
        return address;
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "updates all values of an address.", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address putAddress(@PathVariable UUID customerId, @PathVariable UUID addressId, @RequestBody AddressCreateRequest createRequest) {
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(createRequest, address);

        addressRepository.save(address);
        return address;
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "deletes an address", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address deleteAddress(@PathVariable UUID customerId, @PathVariable UUID addressId) {
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        addressRepository.delete(address);
        return address;
    }
}
