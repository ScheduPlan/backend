package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface AddressService {
    Iterable<Address> getAddresses(UUID customerId,AddressQuery query);

    Address getAddress(UUID customerId, UUID addressId);

    Address createAddress(UUID customerId, AddressCreateRequest createRequest);

    Address patchAddress(UUID customerId, UUID addressId, AddressCreateRequest createRequest);

    Address putAddress(UUID customerId, UUID addressId, AddressCreateRequest createRequest);

    Address deleteAddress(UUID customerId, UUID addressId);
}
