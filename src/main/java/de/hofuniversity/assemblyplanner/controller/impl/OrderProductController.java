package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("customer/{customerId}/orders/{orderId}/products")
public class OrderProductController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderProductController(@Autowired OrderRepository orderRepository, @Autowired ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
            @RequestBody @Parameter(description = "A special object describing the products to add to the order. " +
                    "See the schema description for details. Entries which would result in duplicate associations are skipped. " +
                    "Duplicated IDs in the describing request object are also removed prior to processing the request.")
            ProductAppendRequest request) {

        List<UUID> ids = request.getProducts().stream().distinct().toList();
        Order order = orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
        Set<Product> products = productRepository.findProductsByIds(ids);
        if(products.size() != request.getProducts().size()) {
            throw new ResourceNotFoundException("at least one of the given products was not found");
        }

        for(var product : products) {
            order.getProducts().add(product);
        }

        return orderRepository.save(order);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "deletes an association between a product and an order", responses = {
            @ApiResponse(responseCode = "404", description = "the given product, order or customer was not found " +
                    "or the product to delete was not found in the list of products associated with the given order ")
    })
    @ResponseStatus(HttpStatus.OK)
    public Order deleteProduct(@PathVariable UUID customerId, @PathVariable UUID orderId, @PathVariable UUID productId) {
        Order order = orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
        Product product = order.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        order.getProducts().remove(product);
        return orderRepository.save(order);
    }
}
