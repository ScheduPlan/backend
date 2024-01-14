package de.hofuniversity.assemblyplanner.web;

import de.hofuniversity.assemblyplanner.controller.impl.OrderEventController;
import de.hofuniversity.assemblyplanner.persistence.model.Event;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.EventRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private CustomerRepository customerRepository;

    @Test
    public void shouldDenyOverlapsOnCreate() throws Exception {
        when(eventRepository.findOverlappingEvents(Mockito.any())).thenReturn(Set.of(new Event()));
        when(orderRepository.findByCustomerId(Mockito.any(), Mockito.any())).thenReturn(Optional.of(new Order()));
        mockMvc.perform(post("/customers/" + UUID.randomUUID() + "/orders/" + UUID.randomUUID() + "/events/" + UUID.randomUUID()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldDenyOverlapsOnPatch() throws Exception {
        when(eventRepository.findOverlappingEvents(Mockito.any())).thenReturn(Set.of(new Event()));
        when(orderRepository.findByCustomerId(Mockito.any(), Mockito.any())).thenReturn(Optional.of(new Order()));
        mockMvc.perform(patch("/customers/" + UUID.randomUUID() + "/orders/" + UUID.randomUUID() + "/events/" + UUID.randomUUID()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldDenyOverlapsOnPut() throws Exception {
        when(eventRepository.findOverlappingEvents(Mockito.any())).thenReturn(Set.of(new Event()));
        when(orderRepository.findByCustomerId(Mockito.any(), Mockito.any())).thenReturn(Optional.of(new Order()));
        mockMvc.perform(put("/customers/" + UUID.randomUUID() + "/orders/" + UUID.randomUUID() + "/events/" + UUID.randomUUID()))
                .andExpect(status().is4xxClientError());
    }
}
