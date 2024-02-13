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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class EventServiceImpl implements OrderEventService {

    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, @Autowired OrderRepository orderRepository) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Iterable<Event> getEvents(EventDateQuery dateQuery) {
        LOGGER.info("getting events for query {}", dateQuery);
        if(!dateQuery.isSpecified())
            return eventRepository.findAll();

        Specification<Event> dateSpecification = dateQuery.getEventSpecification();
        return eventRepository.findAll(dateSpecification);
    }

    @Override
    public Iterable<Event> getEvents(UUID customerId, UUID orderId) {
        LOGGER.info("retrieving events for order {}", orderId);
        return eventRepository.findEventsByOrderId(customerId, orderId);
    }


    @Override
    public Event getEvent(UUID customerId, UUID orderId, UUID eventId) {
        LOGGER.info("retrieving event {}", eventId);
        return eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    @Transactional
    public Event createEvent(UUID customerId,
                             UUID orderId,
                             EventCreateRequest createRequest)
    {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("creating event {}", createRequest);

        Event event = new Event(
                createRequest.date(),
                createRequest.endDate(),
                new Description(createRequest.name(), createRequest.description()),
                null,
                createRequest.type(),
                order,
                null
        );

        if(createRequest.endDate() != null && createRequest.date().after(createRequest.endDate())) {
            LOGGER.warn("deleted event {}. The end date was set to a point prior to the start date.", event);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "creating events with an end date prior to the start date is forbidden.");
        }

        event = eventRepository.save(event);
        if(!eventRepository.findOverlappingEvents(event).isEmpty()) {
            eventRepository.delete(event);
            LOGGER.warn("deleted event {}. Overlapping events were found.", event.getId());
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

        LOGGER.info("updating event {} using patch {}", eventId, patchRequest);

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
        LOGGER.info("updated event {}", eventId);
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
        LOGGER.info("updating event {} using update {}", eventId, putRequest);

        BeanUtils.copyProperties(putRequest, event);
        if(!eventRepository.findOverlappingEvents(event).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event overlaps with another event for the same order");

        LOGGER.info("updated event {}", eventId);
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(UUID customerId,
                             UUID orderId,
                             UUID eventId) {
        LOGGER.info("deleting event {}", eventId);
        Event event = eventRepository
                .findEventByOrderId(customerId, orderId, eventId)
                .orElseThrow(ResourceNotFoundException::new);
        LOGGER.info("deleted event {}", eventId);
        eventRepository.delete(event);
    }
}

