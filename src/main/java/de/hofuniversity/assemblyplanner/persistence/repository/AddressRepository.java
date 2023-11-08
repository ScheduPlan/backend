package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends CrudRepository<Address, UUID> {
    @Query("SELECT a FROM Address a WHERE a.ownerId = :customerId")
    Iterable<Address> getAddressesByCustomer(UUID customerId);

    @Query("SELECT a FROM Address a WHERE a.id = :addressId AND a.ownerId = :customerId")
    Optional<Address> findAddressByCustomerId(UUID customerId, UUID addressId);
}
