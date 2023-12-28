package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "country", "street", "streetNumber", "city", "zip", "description", "addressSuffix", "addressType", "customer_id"
        })
})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String country;
    @NonNull
    private String street;
    @NonNull
    private Integer streetNumber;
    @NonNull
    private String city;
    @NonNull
    private String zip;
    private String description;
    private String addressSuffix;
    @NonNull
    private AddressType addressType;

    //by using this dummy value, we prevent JPA from JOINing the two tables.
    // Instead, we can run the query based on the ownerId.
    @Column(name = "customer_id")
    private UUID ownerId;

    public Address(String country, String street, Integer streetNumber, String city, String zip, String description, String addressSuffix, AddressType addressType) {
        this.country = country;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zip = zip;
        this.description = description;
        this.addressSuffix = addressSuffix;
        this.addressType = addressType;
    }


    public Address() {

    }

    public UUID getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressSuffix() {
        return addressSuffix;
    }

    public void setAddressSuffix(String addressSuffix) {
        this.addressSuffix = addressSuffix;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return streetNumber == address.streetNumber && zip == address.zip && Objects.equals(id, address.id) && Objects.equals(country, address.country) && Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(description, address.description) && Objects.equals(addressSuffix, address.addressSuffix) && addressType == address.addressType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country, street, streetNumber, city, zip, description, addressSuffix, addressType);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber=" + streetNumber +
                ", city='" + city + '\'' +
                ", zip=" + zip +
                ", description='" + description + '\'' +
                ", addressSuffix='" + addressSuffix + '\'' +
                ", addressType=" + addressType +
                '}';
    }
}
