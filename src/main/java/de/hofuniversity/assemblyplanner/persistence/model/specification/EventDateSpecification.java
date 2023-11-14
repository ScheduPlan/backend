package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Event;

import java.util.Date;

public class EventDateSpecification extends DateSpecification<Event> {

    public EventDateSpecification(Date startDate, Date endDate) {
        super("date", startDate, endDate);
    }
}
