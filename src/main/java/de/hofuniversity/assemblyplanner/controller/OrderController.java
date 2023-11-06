package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void createOrder(ApplicationReadyEvent evt) {
        orderRepository.save(new Order(123, "", 123, 1.0, OrderState.PLANNED, null, null));
    }
    public OrderController(@Autowired OrderRepository repository, @Autowired CustomerRepository customerRepository) {
        this.orderRepository = repository;
        this.customerRepository = customerRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves all orders matching the criteria defined in the parameters," +
            "or all orders if no restrictions were defined.")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getOrders(@RequestParam(required = false, name = "customer") UUID customerId) {
        return customerId != null ? orderRepository.findByCustomerId(customerId) : orderRepository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "creates a new order", responses = {
            @ApiResponse(
                    responseCode = "415",
                    description = "the customer for which the order is supposed to be created does not exist"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody OrderCreateRequest orderRequest) {
        Customer customer = null;
        if(orderRequest.customerId() != null)
            customer = customerRepository
                    .findById(orderRequest.customerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Order order = new Order(orderRequest.number(),
                orderRequest.description(),
                orderRequest.commissionNumber(),
                orderRequest.weight(),
                OrderState.PLANNED,
                customer,
                null);
        order = orderRepository.save(order);
        return order;
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "patches the specified order. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the order could not be found")
    })
    public Order updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(updateRequest.commissionNumber() != null)
            order.setCommissionNumber(updateRequest.commissionNumber());
        if(updateRequest.number() != null)
            order.setNumber(updateRequest.number());
        if(updateRequest.weight() != null)
            order.setWeight(updateRequest.weight());
        if(updateRequest.description() != null)
            order.setDescription(updateRequest.description());
        if(updateRequest.state() != null)
            order.setState(updateRequest.state());

        return orderRepository.save(order);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "updates all fields of the specified order.", responses = {
            @ApiResponse(responseCode = "404", description = "the order could not be found")
    })
    public Order putOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        order.setCommissionNumber(updateRequest.commissionNumber());
        order.setNumber(updateRequest.number());
        order.setWeight(updateRequest.weight());
        order.setDescription(updateRequest.description());
        order.setState(updateRequest.state());

        return orderRepository.save(order);
    }
}
