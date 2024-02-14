package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import de.hofuniversity.assemblyplanner.persistence.model.specification.AddressSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.AddressRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.service.api.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);

    public AddressServiceImpl(@Autowired AddressRepository addressRepository, @Autowired CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Iterable<Address> getAddresses(UUID customerId, AddressQuery query) {
        return addressRepository.findAll(new AddressSpecification(query, customerId));
    }

    @Override
    public Address getAddress(UUID customerId, UUID addressId) {
        return addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Address createAddress(UUID customerId, AddressCreateRequest createRequest) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("creating address for request {}", createRequest);

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
        LOGGER.info("created address {}", address);
        return address;
    }

    @Override
    public Address patchAddress(UUID customerId, UUID addressId, AddressCreateRequest createRequest) {
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("patching address {} using patch {}", addressId, createRequest);
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
        if(createRequest.streetNumber() != null)
            address.setStreetNumber(createRequest.streetNumber());
        if(createRequest.description() != null)
            address.setDescription(createRequest.description());

        addressRepository.save(address);
        LOGGER.info("patched address {}", address);
        return address;
    }

    @Override
    public Address putAddress(UUID customerId, UUID addressId, AddressCreateRequest createRequest) {
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("updating address {} using update {}", address, createRequest);

        BeanUtils.copyProperties(createRequest, address);

        addressRepository.save(address);
        LOGGER.info("updated address {}", address);
        return address;
    }

    @Override
    public Address deleteAddress(UUID customerId, UUID addressId) {
        LOGGER.info("deleting address {}", addressId);
        Address address = addressRepository
                .findAddressByCustomerId(customerId, addressId)
                .orElseThrow(ResourceNotFoundException::new);

        addressRepository.delete(address);
        LOGGER.info("deleted address {}", addressId);
        return address;
    }
}
