package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventCreateRequest;
import de.hofuniversity.assemblyplanner.service.api.OrderEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders/{orderId}/events")
public class OrderEventController {

    private final OrderEventService eventService;

    @Autowired
    public OrderEventController(OrderEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @Operation(summary = "gets all events for a given order belonging to a customer")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Event> getEvents(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        return eventService.getEvents(customerId, orderId);
    }


    @GetMapping("/{eventId}")
    @Operation(summary = "gets an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Event getEvent(@PathVariable UUID customerId, @PathVariable UUID orderId, @PathVariable UUID eventId) {
        return eventService.getEvent(customerId, orderId, eventId);
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
        return eventService.createEvent(customerId, orderId, createRequest);
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
        return eventService.patchEvent(customerId, orderId, eventId, patchRequest);
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
        return eventService.putEvent(customerId, orderId, eventId, putRequest);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "deletes an event", responses = {
            @ApiResponse(responseCode = "404", description = "the requested event, order or customer was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID customerId,
                          @PathVariable UUID orderId,
                          @PathVariable UUID eventId) {
        eventService.deleteEvent(customerId, orderId, eventId);
    }
}
