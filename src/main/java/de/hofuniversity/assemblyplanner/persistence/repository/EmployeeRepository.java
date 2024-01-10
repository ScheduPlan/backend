package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, UUID> {
    @Query("SELECT e FROM Employee e WHERE e.user.userName = :username")
    Optional<Employee> findByUserName(String username);

    @Query("SELECT e FROM Employee e JOIN e.helpsOn h WHERE h.id = :eventId AND h.order.id = :orderId AND h.order.customer.id = :customerId")
    Set<Employee> findEventHelpers(UUID eventId, UUID orderId, UUID customerId);

    @Query("SELECT e FROM Employee e JOIN e.helpsOn h WHERE h.id = :eventId AND h.order.id = :orderId AND h.order.customer.id = :customerId AND e.id = :employeeId")
    Optional<Employee> findEventHelper(UUID eventId, UUID orderId, UUID customerId, UUID employeeId);
}
