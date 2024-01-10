package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.dto.Helper;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.EmployeeRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.service.EventHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}/orders/{orderId}/events/{eventId}/helpers")
public class EventHelperController {

    private final EmployeeRepository employeeRepository;
    private final EventRepository eventRepository;
    private final EventHelperService eventHelperService;

    @Autowired
    public EventHelperController(EmployeeRepository employeeRepository, EventRepository eventRepository, EventHelperService eventHelperService) {
        this.employeeRepository = employeeRepository;
        this.eventRepository = eventRepository;
        this.eventHelperService = eventHelperService;
    }

    @GetMapping("/{helperId}")
    @Operation(summary = "gets a helper registered on an event", responses = {
            @ApiResponse(responseCode = "404", description = "the customer, order, event or helper was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Helper getHelper(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId,
            @PathVariable UUID eventId,
            @PathVariable UUID helperId) {

        Employee employee = employeeRepository
                .findEventHelper(eventId, orderId, customerId, helperId)
                .orElseThrow(ResourceNotFoundException::new);

        return new Helper(employee);
    }

    @GetMapping
    @Operation(summary = "gets a helper registered on an event", responses = {
            @ApiResponse(responseCode = "404", description = "the customer, order, event or helper was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Set<Helper> getHelpers(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId,
            @PathVariable UUID eventId) {

        return eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new)
                .getHelpers()
                .stream()
                .map(Helper::new).collect(Collectors.toSet());
    }

    @PostMapping
    @Operation(summary = "adds an employee to an event as a helper", responses = {
            @ApiResponse(responseCode = "409", description = "the employee is already registered as a team member or a helper."),
            @ApiResponse(responseCode = "404", description = "the event, order, helper or customer was not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Set<Helper> addHelper(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId,
            @PathVariable UUID eventId,
            @RequestBody ResourceRequest request) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        Employee helper = employeeRepository
                .findById(request.resourceId())
                .orElseThrow(ResourceNotFoundException::new);

        try {
            eventHelperService.addHelper(event, helper);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }

        eventRepository.save(event);
        return event.getHelpers()
                .stream().map(Helper::new).collect(Collectors.toSet());
    }

    @DeleteMapping("/{helperId}")
    @Operation(summary = "removes a helper from an event", responses = {
            @ApiResponse(responseCode = "404", description = "the customer, order, event or helper was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Set<Helper> removeHelper(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId,
            @PathVariable UUID eventId,
            @PathVariable UUID helperId) {

        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        Employee helper = employeeRepository
                .findById(helperId)
                .orElseThrow(ResourceNotFoundException::new);

        try {
            eventHelperService.removeHelper(event, helper);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }

        eventRepository.save(event);
        return event.getHelpers()
                .stream().map(Helper::new).collect(Collectors.toSet());
    }

}
