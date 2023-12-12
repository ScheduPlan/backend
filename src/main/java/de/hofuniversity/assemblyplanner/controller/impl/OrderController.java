package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void createOrder(ApplicationReadyEvent evt) {
        orderRepository.save(new Order(123, "", 123, 1.0, OrderState.PLANNED, null, null));
    }
    public OrderController(@Autowired OrderRepository repository) {
        this.orderRepository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves all orders matching the criteria defined in the parameters," +
            "or all orders if no restrictions were defined.")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getOrders(@RequestParam(required = false, name = "customer") UUID customerId) {
        return customerId != null ? orderRepository.findAllByCustomerId(customerId) : orderRepository.findAll();
    }
}
