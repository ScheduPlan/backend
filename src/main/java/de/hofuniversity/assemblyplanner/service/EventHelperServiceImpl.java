package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.Helper;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.service.api.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventHelperServiceImpl implements de.hofuniversity.assemblyplanner.service.api.EventHelperService {

    private final EventRepository eventRepository;
    private final EmployeeService employeeService;

    @Autowired
    public EventHelperServiceImpl(EventRepository eventRepository, EmployeeService employeeService) {
        this.eventRepository = eventRepository;
        this.employeeService = employeeService;
    }

    @Override
    public Helper getHelper(
            UUID customerId,
            UUID orderId,
            UUID eventId,
            UUID helperId) {

        Employee employee = employeeService.findHelper(customerId, orderId, eventId, helperId);
        return new Helper(employee);
    }

    @Override
    public Set<Helper> getHelpers(
            UUID customerId,
            UUID orderId,
            UUID eventId) {

        return eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new)
                .getHelpers()
                .stream()
                .map(Helper::new).collect(Collectors.toSet());
    }

    @Override
    public Set<Helper> addHelper(UUID customerId, UUID orderId, UUID eventId, ResourceRequest request) {
        Employee helper = employeeService.getEmployee(request.resourceId());

        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        addHelper(event, helper);
        event = eventRepository.save(event);

        return event.getHelpers()
                .stream()
                .map(Helper::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Helper> removeHelper(UUID customerId, UUID orderId, UUID eventId, UUID helperId) {
        Employee helper = employeeService.getEmployee(helperId);

        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        removeHelper(event, helper);
        event = eventRepository.save(event);

        return event.getHelpers()
                .stream()
                .map(Helper::new)
                .collect(Collectors.toSet());
    }

    /**
     * adds a new helper to the event
     * @param event the event to add the helper to
     * @param helper the helper to add to the event
     * @return the modified event instance, which is the object referenced in the {@code event} parameter. Only returned for convenience.
     * @throws IllegalArgumentException if the helper may not be added
     */
    private Event addHelper(Event event, Employee helper) {
        if(event.getHelpers().stream().anyMatch(h -> h.getId().equals(helper.getId())))
            throw new IllegalArgumentException("the employee is already registered as a helper");

        if(helper.getTeam() != null && helper.getTeam().equals(event.getOrder().getTeam()))
            throw new IllegalArgumentException("the employee is already registered as a team member");

        List<Event> overlaps = eventRepository.findOverlappingEvents(event);
        overlaps = overlaps.stream()
                .filter(e -> {
                    boolean isHelper = !e.getHelpers().contains(helper);
                    Order o = e.getOrder();

                    if(o.getTeam() == null)
                        return isHelper;

                    boolean isTeamMember = o.getTeam().getEmployees().stream().anyMatch(member -> member.equals(helper));
                    return !(isHelper || isTeamMember);
                }).collect(Collectors.toList());

        if(!overlaps.isEmpty())
            throw new IllegalArgumentException("the employee is already working on another event in the same timeframe");

        event.getHelpers().add(helper);
        return event;
    }

    private Event removeHelper(Event event, Employee helper) {
        boolean removed = event.getHelpers().removeIf(e -> e.getId().equals(helper.getId()));
        if(!removed)
            throw new IllegalArgumentException("the helper was not found");

        return event;
    }

}
