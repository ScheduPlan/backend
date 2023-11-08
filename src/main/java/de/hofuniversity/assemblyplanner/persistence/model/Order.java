package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer number;
    private String description;
    private Integer commissionNumber;
    private Double weight;
    private OrderState state;

    @ManyToOne private Customer customer;
    @OneToMany private List<Event> events;
    @ManyToMany private List<Product> products;

    public Order(Integer number, String description, Integer commissionNumber, Double weight, OrderState state, Customer customer, List<Event> events) {
        this.number = number;
        this.description = description;
        this.commissionNumber = commissionNumber;
        this.weight = weight;
        this.state = state;
        this.customer = customer;
        this.events = events;
    }

    public Order() {

    }

    public UUID getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCommissionNumber() {
        return commissionNumber;
    }

    public void setCommissionNumber(Integer commissionNumber) {
        this.commissionNumber = commissionNumber;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return number == order.number && commissionNumber == order.commissionNumber && Double.compare(weight, order.weight) == 0 && Objects.equals(id, order.id) && Objects.equals(description, order.description) && state == order.state && Objects.equals(customer, order.customer) && Objects.equals(events, order.events) && Objects.equals(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, description, commissionNumber, weight, state, customer, events, products);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number=" + number +
                ", description='" + description + '\'' +
                ", commissionNumber=" + commissionNumber +
                ", weight=" + weight +
                ", state=" + state +
                ", customer=" + customer +
                ", events=" + events +
                ", products=" + products +
                '}';
    }
}
