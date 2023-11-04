package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String country;
    private String street;
    private int streetNumber;
    private String city;
    private int zip;
    private String description;
    private String addressSuffix;
    private AddressType addressType;

    public Address(String country, String street, int streetNumber, String city, int zip, String description, String addressSuffix, AddressType addressType) {
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

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
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
