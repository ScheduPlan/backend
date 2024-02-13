package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID>, JpaSpecificationExecutor<Order>, PagingAndSortingRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Iterable<Order> findAllByCustomerId(UUID customerId);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.id = :orderId")
    Optional<Order> findByCustomerId(UUID customerId, UUID orderId);

    @Query("SELECT o FROM Order o WHERE o.team.id = :teamId")
    Set<Order> findByTeamId(UUID teamId);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Iterable<Order> findAllByCustomerId(UUID customerId, Sort sort);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.id = :orderId")
    Optional<Order> findByCustomerId(UUID customerId, UUID orderId, Sort sort);

    @Query("SELECT o FROM Order o WHERE o.team.id = :teamId")
    Set<Order> findByTeamId(UUID teamId, Sort sort);
}
