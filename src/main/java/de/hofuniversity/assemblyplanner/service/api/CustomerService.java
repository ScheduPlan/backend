package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerRequest;

import java.util.UUID;

public interface CustomerService {
    Iterable<Customer> getCustomers(CustomerQuery query);

    Customer getCustomer(UUID customerId);

    Customer createCustomer(CustomerRequest customerRequest);

    Customer patchCustomer(UUID customerId, CustomerRequest patchRequest);

    Customer putCustomer(UUID customerId, CustomerRequest putRequest);

    Customer deleteCustomer(UUID customerId);
}
