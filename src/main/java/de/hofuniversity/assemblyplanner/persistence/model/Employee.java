package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.util.Objects;

@Entity
public class Employee extends Person {
    private Integer employeeNumber;
    private String position;
    @ManyToOne private AssemblyTeam team;
    @OneToOne private Address address;
    @Embedded private User user;

    public Employee(String firstName, String lastName, Integer employeeNumber, String position, AssemblyTeam team, Address address, User user) {
        this(firstName, lastName, user);
        this.employeeNumber = employeeNumber;
        this.position = position;
        this.team = team;
        this.address = address;
    }

    public Employee(String firstName, String lastName, User user) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
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
