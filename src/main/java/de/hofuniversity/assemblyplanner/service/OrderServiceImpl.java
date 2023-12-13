package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import de.hofuniversity.assemblyplanner.persistence.model.specification.OrderSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.service.api.OrderSearchEngine;
import de.hofuniversity.assemblyplanner.service.api.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderSearchEngine, OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery orderQuery) {
        return findOrders(orderQuery, null);
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery query, UUID owner) {
        return orderRepository.findAll(new OrderSpecification(query, owner));
    }
}
