package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
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
import de.hofuniversity.assemblyplanner.service.api.CustomerOrderService;
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

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "returns all orders associated to the given customer", responses = {
            @ApiResponse(responseCode = "404", description = "the given customer was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getOrders(@PathVariable UUID customerId, @ParameterObject @ModelAttribute OrderQuery query) {
        return orderService.getOrders(customerId, query);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "gets an order", responses = {
            @ApiResponse(responseCode = "404", description = "the order was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Order getOrder(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        return orderService.getOrder(customerId, orderId);
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
        return orderService.createOrder(customerId, orderRequest);
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "patches the specified order. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the order could not be found")
    })
    public Order updateOrder(@PathVariable UUID customerId, @PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        return orderService.updateOrder(customerId, orderId, updateRequest);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "updates all fields of the specified order.", responses = {
            @ApiResponse(responseCode = "404", description = "the order could not be found")
    })
    public Order putOrder(@PathVariable UUID customerId, @PathVariable UUID orderId, @RequestBody OrderUpdateRequest updateRequest) {
        return orderService.putOrder(customerId, orderId, updateRequest);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDeleteResponse deleteOrder(@PathVariable UUID customerId, @PathVariable UUID orderId) {
        return new OrderDeleteResponse(orderService.deleteOrder(customerId, orderId));
    }
}
