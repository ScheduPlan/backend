package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.Helper;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventHelperService {

    private final EventRepository eventRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EventHelperService(EventRepository eventRepository, EmployeeRepository employeeRepository) {
        this.eventRepository = eventRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * adds a new helper to the event
     * @param event the event to add the helper to
     * @param helper the helper to add to the event
     * @return the modified event instance, which is the object referenced in the {@code event} parameter. Only returned for convenience.
     * @throws IllegalArgumentException if the helper may not be added
     */
    public Event addHelper(Event event, Employee helper) {
        if(event.getHelpers().stream().anyMatch(h -> h.getId().equals(helper.getId())))
            throw new IllegalArgumentException("the employee is already registered as a helper");

        if(helper.getTeam() != null && helper.getTeam().equals(event.getOrder().getTeam()))
            throw new IllegalArgumentException("the employee is already registered as a team member");

        Set<Event> overlaps = eventRepository.findOverlappingEvents(event);
        overlaps = overlaps.stream()
                .filter(e -> {
                    boolean isHelper = !e.getHelpers().contains(helper);
                    Order o = e.getOrder();

                    if(o.getTeam() == null)
                        return isHelper;

                    boolean isTeamMember = o.getTeam().getEmployees().stream().anyMatch(member -> member.equals(helper));
                    return !(isHelper || isTeamMember);
                }).collect(Collectors.toSet());

        if(!overlaps.isEmpty())
            throw new IllegalArgumentException("the employee is already working on another event in the same timeframe");

        event.getHelpers().add(helper);
        return event;
    }

    public Event removeHelper(Event event, Employee helper) {
        boolean removed = event.getHelpers().removeIf(e -> e.getId().equals(helper.getId()));
        if(!removed)
            throw new IllegalArgumentException("the helper was not found");

        return event;
    }
}
