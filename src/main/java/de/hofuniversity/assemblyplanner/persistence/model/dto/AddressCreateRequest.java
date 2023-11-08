package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.AddressType;

public record AddressCreateRequest (
        String country,
        String street,
        Integer streetNumber,
        String city,
        String zip,
        String description,
        String addressSuffix,
        AddressType addressType
) { }
