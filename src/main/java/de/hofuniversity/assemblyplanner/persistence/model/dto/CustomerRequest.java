package de.hofuniversity.assemblyplanner.persistence.model.dto;

public record CustomerRequest(
        String company,
        int customerNumber,
        String description,
        PersonRequest person
) { }
