package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.AddressType;
import org.springframework.lang.NonNull;

public record AddressCreateRequest (
        String country,
        @NonNull
        String street,
        @NonNull
        Integer streetNumber,
        @NonNull
        String city,
        @NonNull
        String zip,
        String description,
        String addressSuffix,
        AddressType addressType
) { }
