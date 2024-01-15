package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.specification.OrderSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.api.CustomerOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final TeamRepository teamRepository;

    public CustomerOrderServiceImpl(
            @Autowired OrderRepository orderRepository,
            @Autowired CustomerRepository customerRepository,
            @Autowired TeamRepository teamRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public Iterable<Order> getOrders(UUID customerId, OrderQuery query) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        return orderRepository.findAll(new OrderSpecification(query, customerId));
    }

    @Override
    public Order getOrder(UUID customerId, UUID orderId) {
        return orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Order createOrder(UUID customerId, OrderCreateRequest orderRequest) {
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

    @Override
    public Order updateOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest) {
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

    @Override
    public Order putOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest) {
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

    @Override
    public Order deleteOrder(UUID customerId, UUID orderId) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);

        order.getProducts().clear();

        orderRepository.delete(order);
        return order;
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery orderQuery) {
        return findOrders(orderQuery, null);
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery query, UUID owner) {
        return orderRepository.findAll(new OrderSpecification(query, owner));
    }

    @Override
    public Set<OrderListItem> getOrdersByTeam(UUID teamId) {
        if(teamId == null)
            throw new IllegalArgumentException("team must not be null");

        return orderRepository
                .findByTeamId(teamId)
                .stream()
                .map(OrderListItem::new)
                .collect(Collectors.toSet());
    }
}