package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Event;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE e.order.id = :orderId AND e.order.customer.id = :customerId")
    Iterable<Event> findEventsByOrderId(UUID customerId, UUID orderId);

    @Query("SELECT e FROM Event e WHERE e.id = :eventId AND e.order.id = :orderId AND e.order.customer.id = :customerId")
    Optional<Event> findEventByOrderId(UUID customerId, UUID orderId, UUID eventId);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN Order o ON o.id = e.order.id " +
            "WHERE ((e.startDate > :#{#event.startDate} AND e.startDate < :#{#event.endDate}) " +
            "OR (e.startDate < :#{#event.startDate} AND e.endDate > :#{#event.startDate})) " +
            "AND e != :#{#event} " +
            "AND (o IS NULL OR e.order = :#{#event.order} OR o.team IS NULL OR o.team = :#{#event.order?.team})")
    Set<Event> findOverlappingEvents(@Param("event") Event event);
}
