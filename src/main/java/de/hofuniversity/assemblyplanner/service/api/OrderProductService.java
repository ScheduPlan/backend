package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductAppendRequest;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import java.util.UUID;

public interface OrderProductService {
    Order addProduct(
            UUID customerId,
            UUID orderId,
            @Valid @Parameter(description = "A special object describing the products to add to the order. " +
                    "See the schema description for details. Entries which would result in duplicate associations are skipped. " +
                    "Duplicated IDs in the describing request object are also removed prior to processing the request.")
            ProductAppendRequest request);

    Order deleteProduct(UUID customerId, UUID orderId, UUID productId);
}
