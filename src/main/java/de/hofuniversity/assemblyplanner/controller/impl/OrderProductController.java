package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductAppendRequest;
import de.hofuniversity.assemblyplanner.service.api.OrderProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("customer/{customerId}/orders/{orderId}/products")
@RolesAllowed({"MANAGER", "ADMINISTRATOR"})
public class OrderProductController {

    private final OrderProductService productService;

    @Autowired
    public OrderProductController(OrderProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "creates an association between a product and an order", responses = {
            @ApiResponse(responseCode = "404", description = "the given product, order or customer was not found " +
                    "or either of them does not belong to the given parent entity.")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Order addProduct(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId,
            @RequestBody @Valid @Parameter(description = "A special object describing the products to add to the order. " +
                    "See the schema description for details. Entries which would result in duplicate associations are skipped. " +
                    "Duplicated IDs in the describing request object are also removed prior to processing the request.")
            ProductAppendRequest request) {

        return productService.addProduct(customerId, orderId, request);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "deletes an association between a product and an order", responses = {
            @ApiResponse(responseCode = "404", description = "the given product, order or customer was not found " +
                    "or the product to delete was not found in the list of products associated with the given order ")
    })
    @ResponseStatus(HttpStatus.OK)
    public Order deleteProduct(@PathVariable UUID customerId, @PathVariable UUID orderId, @PathVariable UUID productId) {
        return productService.deleteProduct(customerId, orderId, productId);
    }
}
