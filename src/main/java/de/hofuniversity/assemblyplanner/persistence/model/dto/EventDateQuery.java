package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.specification.EventDateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record EventDateQuery(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date start, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date end) {
    public boolean isSpecified() {
        return start != null || end != null;
    }
    public Specification<Event> getEventSpecification() {
        return new EventDateSpecification(start, end);
    }
}
