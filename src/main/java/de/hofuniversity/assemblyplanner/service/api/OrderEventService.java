package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventDateQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface OrderEventService {
    Iterable<Event> getEvents(EventDateQuery dateQuery);

    Iterable<Event> getEvents(UUID customerId, UUID orderId);

    Event getEvent(UUID customerId, UUID orderId, UUID eventId);

    Event createEvent(UUID customerId,
                      UUID orderId,
                      EventCreateRequest createRequest);

    Event patchEvent(UUID customerId,
                     UUID orderId,
                     UUID eventId,
                     EventCreateRequest patchRequest);

    Event putEvent(UUID customerId,
                   UUID orderId,
                   UUID eventId,
                   EventCreateRequest putRequest);

    Event deleteEvent(UUID customerId,
                      UUID orderId,
                      UUID eventId);
}
