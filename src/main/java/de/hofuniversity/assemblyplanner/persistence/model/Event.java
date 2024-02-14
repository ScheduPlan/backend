package de.hofuniversity.assemblyplanner.persistence.model;

import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private Date startDate;
    private Date endDate;
    @Embedded private Description description;
    @ManyToOne private Event parentEvent;
    private EventType type;
    @ManyToOne
    private Order order;
    @ManyToMany(mappedBy = "helpsOn")
    private Set<Employee> helpers;

    public Event(Date startDate, Date endDate, Description description, Event parentEvent, EventType type, Order order, Set<Employee> helpers) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.parentEvent = parentEvent;
        this.type = type;
        this.order = order;
        this.helpers = helpers;
    }

    public Event(Date start, Description description, Event parentEvent, EventType type, Order order) {
        this(start, null, description, parentEvent, type, order, null);
    }

    public Event() {

    }

    public Set<Employee> getHelpers() {
        return helpers;
    }

    public void setHelpers(Set<Employee> helpers) {
        this.helpers = helpers;
    }

    public UUID getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date end) {
        this.endDate = end;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Event getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(Event parentEvent) {
        this.parentEvent = parentEvent;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate) && Objects.equals(description, event.description) && Objects.equals(parentEvent, event.parentEvent) && type == event.type && Objects.equals(order, event.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, description, parentEvent, type, order);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", start=" + startDate +
                ", end=" + endDate +
                ", description=" + description +
                ", parentEvent=" + parentEvent +
                ", type=" + type +
                ", order=" + order +
                '}';
    }

}
