package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderDeleteResponse;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.api.OrderSearchEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders")
public class CustomerOrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final TeamRepository teamRepository;
    private final OrderSearchEngine orderSearchEngine;

    public CustomerOrderController(
            @Autowired OrderRepository orderRepository,
            @Autowired CustomerRepository customerRepository,
            @Autowired TeamRepository teamRepository,
            OrderSearchEngine orderSearchEngine) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.teamRepository = teamRepository;
        this.orderSearchEngine = orderSearchEngine;
    }

    @GetMapping
    @Operation(summary = "returns all orders associated to the given customer", responses = {
            @ApiResponse(responseCode = "404", description = "the given customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getOrders(@PathVariable UUID customerId, @ParameterObject @ModelAttribute OrderQuery query) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        return orderSearchEngine.findOrders(query, customer.getId());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "gets an order", responses = {
            @ApiResponse(responseCode = "404", description = "the order was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Order getOrder(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        return orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "creates a new order for the customer", responses = {
            @ApiResponse(
                    responseCode = "404",
                    description = "the customer or team for which the order is supposed to be created does not exist"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@PathVariable UUID customerId, @RequestBody @Valid OrderCreateRequest orderRequest) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        AssemblyTeam team = null;
        if(orderRequest.teamId() != null)
            team = teamRepository
                    .findById(orderRequest.teamId())
                    .orElseThrow(ResourceNotFoundException::new);

        Order order = new Order(orderRequest.number(),
                orderRequest.description(),
                orderRequest.commissionNumber(),
                orderRequest.weight(),
                OrderState.PLANNED,
                customer,
                null,
                team,
                orderRequest.plannedDuration());

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
                .orElseThrow(ResourceNotFoundException::new);

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
        if(updateRequest.teamId() != null) {
            AssemblyTeam team = teamRepository
                    .findById(updateRequest.teamId())
                    .orElseThrow(ResourceNotFoundException::new);
            order.setTeam(team);
        }
        if(updateRequest.plannedDuration() != null){
            order.setPlannedDuration(updateRequest.plannedDuration());
        }

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
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(updateRequest, order, "teamId");

        AssemblyTeam team = null;
        if(updateRequest.teamId() != null) {
            team = teamRepository
                    .findById(updateRequest.teamId())
                    .orElseThrow(ResourceNotFoundException::new);
        }
        order.setTeam(team);

        return orderRepository.save(order);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDeleteResponse deleteOrder(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);

        order.getProducts().clear();

        OrderDeleteResponse response = new OrderDeleteResponse(order);

        orderRepository.delete(order);
        return response;
    }
}
