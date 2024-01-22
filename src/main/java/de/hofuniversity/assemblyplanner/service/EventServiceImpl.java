package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EventDateQuery;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.service.api.OrderEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class EventServiceImpl implements OrderEventService {

    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, @Autowired OrderRepository orderRepository) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Iterable<Event> getEvents(EventDateQuery dateQuery) {
        if(!dateQuery.isSpecified())
            return eventRepository.findAll();

        Specification<Event> dateSpecification = dateQuery.getEventSpecification();
        return eventRepository.findAll(dateSpecification);
    }

    @Override
    public Iterable<Event> getEvents(UUID customerId, UUID orderId) {
        return eventRepository.findEventsByOrderId(customerId, orderId);
    }


    @Override
    public Event getEvent(UUID customerId, UUID orderId, UUID eventId) {
        return eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Event createEvent(UUID customerId,
                             UUID orderId,
                             EventCreateRequest createRequest)
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
                order,
                null
        );

        if(createRequest.endDate() != null && createRequest.date().after(createRequest.endDate()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "creating events with an end date prior to the start date is forbidden.");

        if(!eventRepository.findOverlappingEvents(event).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");
        }

        return eventRepository.save(event);
    }

    @Override
    public Event patchEvent(UUID customerId,
                            UUID orderId,
                            UUID eventId,
                            EventCreateRequest patchRequest) {
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
        if(patchRequest.endDate() != null)
            event.setEndDate(patchRequest.endDate());

        if(!eventRepository.findOverlappingEvents(event).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");

        return eventRepository.save(event);
    }

    @Override
    public Event putEvent(UUID customerId,
                          UUID orderId,
                          UUID eventId,
                          EventCreateRequest putRequest) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(putRequest, event);
        if(!eventRepository.findOverlappingEvents(event).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(UUID customerId,
                             UUID orderId,
                             UUID eventId) {
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);

        eventRepository.delete(event);
    }
}

