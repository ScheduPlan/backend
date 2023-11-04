package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Customer extends Person {
    private String company;
    private int customerNumber;
    @OneToMany private List<Address> addresses;
    @OneToMany private List<Order> orders;
    private String description;

    public Customer(String company, int customerNumber, String description) {
        this.company = company;
        this.customerNumber = customerNumber;
        this.description = description;
    }

    public Customer() {

    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompany() {
        return !(company == null || company.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return customerNumber == customer.customerNumber && Objects.equals(company, customer.company) && Objects.equals(addresses, customer.addresses) && Objects.equals(orders, customer.orders) && Objects.equals(description, customer.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), company, customerNumber, addresses, orders, description);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "company='" + company + '\'' +
                ", customerNumber=" + customerNumber +
                ", addresses=" + addresses +
                ", orders=" + orders +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
