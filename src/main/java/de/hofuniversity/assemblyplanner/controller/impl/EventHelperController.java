package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.dto.Helper;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ResourceRequest;
import de.hofuniversity.assemblyplanner.service.api.EventHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders/{orderId}/events/{eventId}/helpers")
public class EventHelperController {


    private final EventHelperService eventHelperService;

    @Autowired
    public EventHelperController(EventHelperService eventHelperService) {
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

        return eventHelperService.getHelper(customerId, orderId, eventId, helperId);
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

        return eventHelperService.getHelpers(customerId, orderId, eventId);
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

        return eventHelperService.addHelper(customerId, orderId, eventId, request);
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

        return eventHelperService.removeHelper(customerId, orderId, eventId, helperId);
    }

}
