package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record CustomerQuery(String firstName, String lastName, String company, Integer customerNumber, AddressQuery address) {

}
