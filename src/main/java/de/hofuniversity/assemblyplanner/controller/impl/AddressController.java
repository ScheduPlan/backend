package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import de.hofuniversity.assemblyplanner.service.api.AddressService;
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
@RequestMapping("/customers/{customerId}/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves all addresses for a given customer")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Address> getAddresses(@PathVariable UUID customerId, @ParameterObject @ModelAttribute AddressQuery query) {
        return addressService.getAddresses(customerId, query);
    }

    @GetMapping(value = "/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves a single address for the given customer", responses = {
            @ApiResponse(responseCode = "404", description = "the given customer or address wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address getAddress(@PathVariable UUID customerId, @PathVariable UUID addressId) {
        return addressService.getAddress(customerId, addressId);
    }

    @PostMapping
    @Operation(summary = "creates a new address for this customer", responses = {
            @ApiResponse(responseCode = "404", description = "the customer wasn't found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Address createAddress(@PathVariable UUID customerId, @RequestBody @Valid AddressCreateRequest createRequest) {
        return addressService.createAddress(customerId, createRequest);
    }

    @PatchMapping(value = "/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "patches an address. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address patchAddress(@PathVariable UUID customerId, @PathVariable UUID addressId, @RequestBody AddressCreateRequest createRequest) {
        return addressService.patchAddress(customerId, addressId, createRequest);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "updates all values of an address.", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address putAddress(@PathVariable UUID customerId, @PathVariable UUID addressId, @RequestBody AddressCreateRequest createRequest) {
        return addressService.putAddress(customerId, addressId, createRequest);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "deletes an address", responses = {
            @ApiResponse(responseCode = "404", description = "the address or customer wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Address deleteAddress(@PathVariable UUID customerId, @PathVariable UUID addressId) {
        return addressService.deleteAddress(customerId, addressId);
    }
}
