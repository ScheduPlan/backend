package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {
    @Query("SELECT e FROM Employee e WHERE e.user.username = :username")
    Optional<Employee> findByUserName(String username);

    @Query("SELECT e FROM Employee e WHERE e.user.role = :role")
    Iterable<Employee> findByRole(Role role);

    @Query("SELECT e FROM Employee e JOIN e.helpsOn h WHERE h.id = :eventId AND h.order.id = :orderId AND h.order.customer.id = :customerId")
    Set<Employee> findEventHelpers(UUID eventId, UUID orderId, UUID customerId);

    @Query("SELECT e FROM Employee e JOIN e.helpsOn h WHERE h.id = :eventId AND h.order.id = :orderId AND h.order.customer.id = :customerId AND e.id = :employeeId")
    Optional<Employee> findEventHelper(UUID eventId, UUID orderId, UUID customerId, UUID employeeId);
}
