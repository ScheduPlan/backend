package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.EventType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record EventCreateRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date,
        EventType type,
        String name,
        String description
) { }
