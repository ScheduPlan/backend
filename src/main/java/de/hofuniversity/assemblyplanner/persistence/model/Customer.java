package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Customer extends Person {
    private String company;
    @Column(unique = true)
    private int customerNumber;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private List<Address> addresses;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "customer")
    @JsonIgnore
    private List<Order> orders;
    private String description;
    private String email;
    private String phoneNumber;

    public Customer(String company, int customerNumber, String description, String firstName, String lastName, String email, String phoneNumber) {
        this.company = company;
        this.customerNumber = customerNumber;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Customer() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
                ", description='" + description + '\'' +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
