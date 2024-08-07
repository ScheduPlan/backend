package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hofuniversity.assemblyplanner.persistence.model.notification.EmployeeNotification;
import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Set;

@Entity
public class Employee extends Person {
    @Column(unique = true)
    @NonNull
    private Integer employeeNumber;
    private String position;
    @ManyToOne private AssemblyTeam team;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) private Address address;
    @Embedded private User user;
    @ManyToMany(mappedBy = "helpers")
    @JsonIgnore
    private Set<Event> helpsOn;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<EmployeeNotification> notifications;

    public Employee(String firstName, String lastName, Integer employeeNumber, String position, AssemblyTeam team, Address address, User user, Set<Event> helpsOn) {
        this(firstName, lastName, user);
        this.employeeNumber = employeeNumber;
        this.position = position;
        this.team = team;
        this.address = address;
        this.helpsOn = helpsOn;
    }

    public Employee(String firstName, String lastName, User user) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Set<Event> getHelpsOn() {
        return helpsOn;
    }

    public void setHelpsOn(Set<Event> helpsOn) {
        this.helpsOn = helpsOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Employee() {

    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public AssemblyTeam getTeam() {
        return team;
    }

    public void setTeam(AssemblyTeam team) {
        this.team = team;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<EmployeeNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<EmployeeNotification> notifications) {
        this.notifications = notifications;
    }

    public EmployeeNotification addNotification(Notification<?> notification) {
        EmployeeNotification employeeNotification = new EmployeeNotification(notification, this);
        notifications.add(employeeNotification);
        notification.getRecipients().add(employeeNotification);
        return employeeNotification;
    }

    public EmployeeNotification removeNotification(Notification<?> notification) {
        EmployeeNotification employeeNotification = this.getNotifications()
                .stream()
                .filter(n -> n.getNotification().equals(notification))
                .findFirst()
                .orElse(null);

        if(employeeNotification == null)
            return null;

        employeeNotification.getNotification().getRecipients().remove(employeeNotification);
        this.getNotifications().remove(employeeNotification);
        return employeeNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeNumber, employee.employeeNumber) && Objects.equals(position, employee.position) && Objects.equals(team, employee.team) && Objects.equals(address, employee.address) && Objects.equals(user, employee.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), employeeNumber, position, team, address, user);
    }
}
