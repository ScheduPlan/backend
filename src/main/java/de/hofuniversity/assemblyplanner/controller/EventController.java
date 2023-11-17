package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventDateQuery;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(@Autowired EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    @Operation(summary = "gets all events", description = "gets all events or, if specified, retrieves events " +
            "using the given timeframe specification. If only \"end\" is specified, retrieves all Event objects " +
            "BEFORE end. If only \"start\" is specified, retrieves all Event objects AFTER start. All dates sent MUST " +
            "conform to ISO-8601 Date-Time formatting (yyyy-MM-dd'T'HH:mm:ss.SSSXXX), where XXX can also be replaced " +
            "by 'Z' in order to indicate UTC time.")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Event> getEvents(@ParameterObject @ModelAttribute EventDateQuery dateQuery) {
        if(!dateQuery.isSpecified())
            return eventRepository.findAll();

        Specification<Event> dateSpecification = dateQuery.getEventSpecification();
        return eventRepository.findAll(dateSpecification);
    }
}

