package de.hofuniversity.assemblyplanner.persistence.model.notification;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.Objects;

@Entity
@IdClass(EmployeeNotificationKey.class)
public class EmployeeNotification {

    @Id
    @ManyToOne
    private Notification notification;
    @Id
    @ManyToOne
    private Employee employee;
    private boolean read;

    public EmployeeNotification(Notification notification, Employee employee) {
        this.notification = notification;
        this.employee = employee;
    }

    public EmployeeNotification(){}

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeNotification that = (EmployeeNotification) o;
        return read == that.read && Objects.equals(notification, that.notification) && Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notification, employee, read);
    }
}
