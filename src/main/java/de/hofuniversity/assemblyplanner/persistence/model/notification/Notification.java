package de.hofuniversity.assemblyplanner.persistence.model.notification;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Inheritance
public abstract class Notification<T extends NotificationPayload> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeNotification> recipients;
    @CreationTimestamp
    private Date createdAt;
    private String type;

    public Notification(Iterable<Employee> recipients, String type) {
        this.recipients = new HashSet<>();
        if(recipients != null)
            recipients.forEach(this::addRecipient);
        this.type = type;
    }

    public Notification() {

    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getNotificationType() {
        return getClass().getName();
    }

    public Set<EmployeeNotification> getRecipients() {
        return recipients;
    }

    public EmployeeNotification addRecipient(Employee recipient) {
        EmployeeNotification notification = new EmployeeNotification(this, recipient);
        recipients.add(notification);
        recipient.getNotifications().add(notification);
        return notification;
    }

    public EmployeeNotification removeRecipient(Employee recipient) {
        EmployeeNotification employeeNotification = this.getRecipients()
                .stream()
                .filter(n -> n.getEmployee().equals(recipient))
                .findFirst()
                .orElse(null);

        if(employeeNotification == null)
            return null;

        employeeNotification.getEmployee().getNotifications().remove(employeeNotification);
        this.getRecipients().remove(employeeNotification);
        return employeeNotification;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setRecipients(Set<EmployeeNotification> recipients) {
        this.recipients = recipients;
    }

    public boolean didRead(Employee recipient) {
        Optional<EmployeeNotification> notification = findRecipientRelation(recipient);

        return notification.isPresent() && notification.get().isRead();
    }

    public boolean setRead(Employee recipient, boolean read) {
        Optional<EmployeeNotification> notification = findRecipientRelation(recipient);
        if(notification.isEmpty())
            return false;

        notification.get().setRead(read);
        return true;
    }

    private Optional<EmployeeNotification> findRecipientRelation(Employee recipient) {
        return recipients
                .stream()
                .filter(e -> e.getEmployee().equals(recipient))
                .findFirst();
    }

    public void setType(String type) {
        this.type = type;
    }

    public Notification<T> withRecipients(Iterable<Employee> recipients) {
        this.getRecipients().clear();
        recipients.forEach(this::addRecipient);
        return this;
    }

    public Notification<T> withType(String type) {
        this.type = type;
        return this;
    }

    public abstract T getPayload();
}
