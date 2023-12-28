package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.EventType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.util.Date;

public record EventCreateRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NonNull
        Date date,
        EventType type,
        @NonNull
        String name,
        String description
) { }
