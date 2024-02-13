package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AllOrdersQuery;
import de.hofuniversity.assemblyplanner.service.api.CustomerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CustomerOrderService orderService;

    @Autowired
    public OrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "retrieves all orders matching the criteria defined in the parameters," +
            "or all orders if no restrictions were defined.")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getOrders(
            @RequestParam(required = false, name = "customer") UUID customerId,
            @ParameterObject @ModelAttribute AllOrdersQuery orderQuery,
            @RequestParam(required = false, name = "sortedBy")
            @Parameter(description = "describes how to sort the values. Field order is important." +
                    " A \"_\" indicates that the property belongs to the sub-object defined by the prefix.",
                    example = "\"customer_company\" or \"commissionNumber\"")
            String[] sorting)
    {
        if(sorting == null)
            sorting = new String[0];

        return orderService.findOrders(orderQuery, sorting);
    }

}
