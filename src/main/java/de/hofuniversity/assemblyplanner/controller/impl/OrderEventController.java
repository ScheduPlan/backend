package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders/{orderId}/events")
public class OrderEventController {

    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    public OrderEventController(@Autowired EventRepository eventRepository, @Autowired OrderRepository orderRepository) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    @Operation(summary = "gets all events for a given order belonging to a customer")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Event> getEvents(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        return eventRepository.findEventsByOrderId(customerId, orderId);
    }


    @GetMapping("/{eventId}")
    @Operation(summary = "gets an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Event getEvent(@PathVariable UUID customerId, @PathVariable UUID orderId, @PathVariable UUID eventId) {
        return eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "creates an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested order or customer was not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@PathVariable UUID customerId,
                             @PathVariable UUID orderId,
                             @RequestBody @Valid EventCreateRequest createRequest)
    {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);

        Event event = new Event(
                createRequest.date(),
                createRequest.endDate(),
                new Description(createRequest.name(), createRequest.description()),
                null,
                createRequest.type(),
                order
        );

        if(createRequest.endDate() != null && createRequest.date().after(createRequest.endDate()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "creating events with an end date prior to the start date is forbidden.");

        if(!eventRepository.findOverlappingEvents(event).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");
        }

        return eventRepository.save(event);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "updates an event. NULL values are ignored", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Event patchEvent(@PathVariable UUID customerId,
                            @PathVariable UUID orderId,
                            @PathVariable UUID eventId,
                            @RequestBody EventCreateRequest patchRequest) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        if(patchRequest.date() != null)
            event.setStartDate(patchRequest.date());
        if(patchRequest.type() != null)
            event.setType(patchRequest.type());
        if(patchRequest.name() != null)
            event.getDescription().setName(patchRequest.name());
        if(patchRequest.description() != null)
            event.getDescription().setDescription(patchRequest.description());

        if(!eventRepository.findOverlappingEvents(event).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");

        return eventRepository.save(event);
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "updates an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Event putEvent(@PathVariable UUID customerId,
                            @PathVariable UUID orderId,
                            @PathVariable UUID eventId,
                            @RequestBody EventCreateRequest putRequest) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(putRequest, event);
        if(!eventRepository.findOverlappingEvents(event).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");

        return eventRepository.save(event);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "deletes an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Event deleteEvent(@PathVariable UUID customerId,
                          @PathVariable UUID orderId,
                          @PathVariable UUID eventId) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        eventRepository.delete(event);
        return event;
    }
}
