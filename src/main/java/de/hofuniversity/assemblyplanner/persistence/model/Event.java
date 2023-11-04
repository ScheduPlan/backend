package de.hofuniversity.assemblyplanner.persistence.model;

import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Date date;
    @Embedded private Description description;
    @ManyToOne private Event parentEvent;
    private EventType type;
    @ManyToOne
    private Order order;

    public Event(Date date, Description description, Event parentEvent, EventType type, Order order) {
        this.date = date;
        this.description = description;
        this.parentEvent = parentEvent;
        this.type = type;
        this.order = order;
    }

    public Event() {

    }

    public UUID getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                ", description=" + description +
                ", parentEvent=" + parentEvent +
                ", type=" + type +
                ", order=" + order +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(date, event.date) && Objects.equals(description, event.description) && Objects.equals(parentEvent, event.parentEvent) && type == event.type && Objects.equals(order, event.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, description, parentEvent, type, order);
    }
}
