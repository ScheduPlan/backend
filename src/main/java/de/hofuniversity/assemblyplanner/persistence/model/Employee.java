package de.hofuniversity.assemblyplanner.persistence.model;

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

    public Employee(Integer employeeNumber, String position, AssemblyTeam team, Address address) {
        this.employeeNumber = employeeNumber;
        this.position = position;
        this.team = team;
        this.address = address;
    }

    public Employee() {

    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
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
        Employee employee = (Employee) o;
        return super.equals(o) && employeeNumber.equals(employee.employeeNumber) && Objects.equals(position, employee.position) && Objects.equals(team, employee.team) && Objects.equals(address, employee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), employeeNumber, position, team, address);
    }
}
