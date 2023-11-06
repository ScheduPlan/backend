package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Iterable<Order> findByCustomerId(UUID customerId);
}
