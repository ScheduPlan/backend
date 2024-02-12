package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.*;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.TeamDescription;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.util.DateUtil;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventPersistenceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Event createTestEvent(Date start, Date end) {
        return new Event(
                start,
                end,
                new Description("test", "test"),
                null,
                EventType.ASSEMBLY,
                null,
                Set.of()
        );
    }

    @Test
    public void shouldFindDirectlyOverlappingEvent() {
        Event event = createTestEvent(
                new Date(),
                new Date(Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli())
        );

        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                null
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        var overlaps = eventRepository.findOverlappingEvents(event);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindEndOverlappingEvent() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().minus(Duration.ofMinutes(10)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli())
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindStartOverlappingEvent() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(20)).toEpochMilli())
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindEntirelyOverlappingEvent() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().minus(Duration.ofMinutes(10)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(20)).toEpochMilli())
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindOverlappingTimeSpan() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldNotFindNotOverlappingEvent() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(20)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(21)).toEpochMilli())
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(0);
    }

    @Test
    public void shouldNotFindInstants() {
        Event event = createTestEvent(
                new Date(),
                null
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli()),
                null
        );

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(0);
    }

    @Test
    public void shouldFindOverlapsOnOverlappingEventNullOrder() {
        Order firstOrder = new Order(123, "", "123", 1.0, OrderState.PLANNED, null, Set.of(), null, null);
        orderRepository.save(firstOrder);

        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
        );

        event.setOrder(firstOrder);

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindOverlapsOnParentEventNullOrder() {
        Order firstOrder = new Order(123, "", "123", 1.0, OrderState.PLANNED, null, Set.of(), null, null);
        orderRepository.save(firstOrder);

        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
        );

        overlappingEvent.setOrder(firstOrder);

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindOverlapsOnParentEventSameTeam() {
        AssemblyTeam team = new AssemblyTeam(new TeamDescription("test", "test"), List.of(), null);
        teamRepository.save(team);

        Order firstOrder = new Order(123, "", "123", 1.0, OrderState.PLANNED, null, Set.of(), team, null);
        Order secondOrder = new Order(456, "", "123", 1.0, OrderState.PLANNED, null, Set.of(), team, null);
        firstOrder = orderRepository.save(firstOrder);
        secondOrder = orderRepository.save(secondOrder);

        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event overlappingEvent = createTestEvent(
                new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
        );

        event.setOrder(firstOrder);
        overlappingEvent.setOrder(secondOrder);

        eventRepository.save(event);
        eventRepository.save(overlappingEvent);

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(1)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

    @Test
    public void shouldFindMultipleOverlaps() {
        Event event = createTestEvent(
                new Date(),
                DateUtil.addTemporalAmount(new Date(), Duration.ofMinutes(10))
        );
        Event[] overlaps = new Event[] {
            createTestEvent(
                    new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                    new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
            ),
            createTestEvent(
                    new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                    new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
            ),
            createTestEvent(
                    new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli()),
                    new Date(Instant.now().plus(Duration.ofMinutes(2)).toEpochMilli())
            )
        };

        eventRepository.save(event);

        eventRepository.saveAll(Arrays.asList(overlaps));

        assertThat(eventRepository.findOverlappingEvents(event))
                .hasSize(overlaps.length)
                .allMatch(e -> !e.getId().equals(event.getId()));
    }

}
