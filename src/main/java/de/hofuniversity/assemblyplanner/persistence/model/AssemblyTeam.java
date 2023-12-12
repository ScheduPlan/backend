package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class AssemblyTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded private Description description;
    @OneToMany(mappedBy = "team") private List<Employee> employees;
    @OneToMany(mappedBy = "team") @JsonBackReference private List<Order> orders;

    public AssemblyTeam(Description description, List<Employee> employees, List<Order> orders) {
        this.description = description;
        this.employees = employees;
        this.orders = orders;
    }

    public AssemblyTeam() {}

    public UUID getId() {
        return id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Iterable<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssemblyTeam that = (AssemblyTeam) o;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) && Objects.equals(employees, that.employees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, employees);
    }

    @Override
    public String toString() {
        return "AssemblyTeam{" +
                "id=" + id +
                ", description=" + description +
                ", employees=" + employees +
                '}';
    }
}
