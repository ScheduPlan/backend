package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;

import java.util.UUID;

public interface OrderSearchEngine {
    Iterable<Order> findOrders(OrderQuery orderQuery);
    Iterable<Order> findOrders(OrderQuery orderQuery, UUID ownerId);
}
