package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NotificationRepository extends PagingAndSortingRepository<Notification<?>, UUID>, CrudRepository<Notification<?>, UUID> {
    @Query("SELECT n.notification FROM EmployeeNotification n WHERE n.employee.id = :employeeId")
    Set<Notification<?>> getNotificationsByUserId(UUID employeeId);

    @Query("SELECT n.notification FROM EmployeeNotification n WHERE n.employee.id = :employeeId AND n.notification.createdAt > :after")
    Set<Notification<?>> getNotificationsByUserId(UUID employeeId, Date after);

    @Query("SELECT n.notification FROM EmployeeNotification n WHERE n.employee.id = :employeeId AND n.notification.id = :notificationId")
    Optional<Notification<?>> getNotificationByUserId(UUID notificationId, UUID employeeId);
}
