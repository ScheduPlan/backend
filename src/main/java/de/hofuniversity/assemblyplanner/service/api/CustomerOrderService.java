package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.*;

import java.util.Set;
import java.util.UUID;

public interface CustomerOrderService {
    Iterable<Order> getOrders(UUID customerId, OrderQuery query);

    Order getOrder(UUID customerId, UUID orderId);

    Order createOrder(UUID customerId, OrderCreateRequest orderRequest);

    Order updateOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest);

    Order putOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest);

    Order deleteOrder(UUID customerId, UUID orderId);

    Iterable<Order> findOrders(OrderQuery orderQuery);

    Iterable<Order> findOrders(OrderQuery query, UUID owner);

    Iterable<Order> findOrders(AllOrdersQuery query, String... order);

    Set<OrderListItem> getOrdersByTeam(UUID teamId);
}
