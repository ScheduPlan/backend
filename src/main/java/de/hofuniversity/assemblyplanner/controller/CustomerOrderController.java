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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/customer/{customerId}/orders")
public class CustomerOrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerOrderController(@Autowired OrderRepository orderRepository, @Autowired CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "creates a new order for the customer", responses = {
            @ApiResponse(
                    responseCode = "415",
                    description = "the customer for which the order is supposed to be created does not exist"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@PathVariable UUID customerId, @RequestBody OrderCreateRequest orderRequest) {
        Customer customer = customerRepository
                .findById(orderRequest.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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
    public Order updateOrder(@PathVariable UUID customerId, @PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
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
    public Order putOrder(@PathVariable UUID customerId, @PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BeanUtils.copyProperties(updateRequest, order);

        return orderRepository.save(order);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Order deleteOrder(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        orderRepository.delete(order);
        return order;
    }
}
