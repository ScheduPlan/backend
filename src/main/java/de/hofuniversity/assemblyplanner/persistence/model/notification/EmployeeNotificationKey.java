package de.hofuniversity.assemblyplanner.persistence.model.notification;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EmployeeNotificationKey implements Serializable {

    private UUID notification;
    private UUID employee;

    public EmployeeNotificationKey(UUID notificationId, UUID employeeId) {
        this.notification = notificationId;
        this.employee = employeeId;
    }

    public EmployeeNotificationKey(){}

    public UUID getNotification() {
        return notification;
    }

    public UUID getEmployee() {
        return employee;
    }

    public void setNotificationId(UUID notificationId) {
        this.notification = notificationId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employee = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeNotificationKey that = (EmployeeNotificationKey) o;
        return Objects.equals(notification, that.notification) && Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notification, employee);
    }
}
