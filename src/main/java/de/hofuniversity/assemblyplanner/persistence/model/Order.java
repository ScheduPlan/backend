package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private Integer number;
    private String description;
    private String commissionNumber;
    private Double weight;
    private Double plannedDuration;
    private Date plannedExecutionDate;
    @NonNull
    private OrderState state;

    @ManyToOne
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
    @JsonIgnore
    private Set<Event> events;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnore
    private Set<Product> products;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private AssemblyTeam team;

    public Order(Integer number, String description, String commissionNumber, Double weight, OrderState state, Customer customer, Set<Event> events, AssemblyTeam team, Double plannedDuration, Date plannedExecutionDate) {
        this.number = number;
        this.description = description;
        this.commissionNumber = commissionNumber;
        this.weight = weight;
        this.state = state;
        this.customer = customer;
        this.events = events;
        this.team = team;
        this.plannedDuration = plannedDuration;
        this.plannedExecutionDate = plannedExecutionDate;
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

    public String getCommissionNumber() {
        return commissionNumber;
    }

    public void setCommissionNumber(String commissionNumber) {
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

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public AssemblyTeam getTeam() {
        return team;
    }

    public void setTeam(AssemblyTeam team) {
        this.team = team;
    }

    public Double getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(Double plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public Date getPlannedExecutionDate() {
        return plannedExecutionDate;
    }

    public void setPlannedExecutionDate(Date plannedExecutionDate) {
        this.plannedExecutionDate = plannedExecutionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return number.equals(order.number) && commissionNumber.equals(order.commissionNumber) && Double.compare(weight, order.weight) == 0 && Objects.equals(id, order.id) && Objects.equals(description, order.description) && state == order.state && Objects.equals(customer, order.customer) && Objects.equals(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, description, commissionNumber, weight, state, customer, products);
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
                ", products=" + products +
                '}';
    }
}
