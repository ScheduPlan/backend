package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.dto.*;
import de.hofuniversity.assemblyplanner.persistence.model.notification.OrderNotification;
import de.hofuniversity.assemblyplanner.persistence.model.notification.OrderNotificationPayload;
import de.hofuniversity.assemblyplanner.persistence.model.specification.AllOrdersSpecification;
import de.hofuniversity.assemblyplanner.persistence.model.specification.RestrictedOrderSpecification;
import de.hofuniversity.assemblyplanner.persistence.repository.CustomerRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.service.api.CustomerOrderService;
import de.hofuniversity.assemblyplanner.service.api.NotificationService;
import org.hibernate.boot.model.source.spi.Sortable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    public CustomerOrderServiceImpl(
            @Autowired OrderRepository orderRepository,
            @Autowired CustomerRepository customerRepository,
            @Autowired TeamRepository teamRepository,
            @Autowired NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Iterable<Order> getOrders(UUID customerId, OrderQuery query) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(ResourceNotFoundException::new);
        LOGGER.info("retrieving orders for customer {} using query {}", customerId, query);
        return orderRepository.findAll(new RestrictedOrderSpecification(query, customerId));
    }

    @Override
    public Order getOrder(UUID customerId, UUID orderId) {
        LOGGER.info("retrieving order {} for customer {}", orderId, customerId);
        return orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    @Transactional
    public Order createOrder(UUID customerId, OrderCreateRequest orderRequest) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("creating order for customer {} using create request {}", customerId, orderRequest);
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
                orderRequest.plannedDuration(),
                orderRequest.plannedExecutionDate());

        order = orderRepository.save(order);
        LOGGER.info("created order {}", order);
        if(team != null) {
            sendNotification(OrderNotification.Type.CREATED, order, customerId);
        }

        return order;
    }

    @Override
    public Order updateOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);
        LOGGER.info("updating order for customer {} using patch request {}", customerId, updateRequest);
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
        if(updateRequest.plannedExecutionDate() != null)
            order.setPlannedExecutionDate(updateRequest.plannedExecutionDate());

        sendNotification(OrderNotification.Type.UPDATED, order, customerId);

        return orderRepository.save(order);
    }

    @Override
    public Order putOrder(UUID customerId, UUID orderId, OrderUpdateRequest updateRequest) {
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);
        LOGGER.info("updating order for customer {} using update request {}", customerId, updateRequest);
        BeanUtils.copyProperties(updateRequest, order, "teamId");

        AssemblyTeam team = null;
        if(updateRequest.teamId() != null) {
            team = teamRepository
                    .findById(updateRequest.teamId())
                    .orElseThrow(ResourceNotFoundException::new);
        }
        order.setTeam(team);

        sendNotification(OrderNotification.Type.UPDATED, order, customerId);

        return orderRepository.save(order);
    }

    @Override
    public Order deleteOrder(UUID customerId, UUID orderId) {
        LOGGER.info("deleting order {}", orderId);
        Order order = orderRepository
                .findByCustomerId(customerId, orderId)
                .orElseThrow(ResourceNotFoundException::new);

        order.getProducts().clear();

        orderRepository.delete(order);
        LOGGER.info("deleted order {}", orderId);
        return order;
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery orderQuery) {
        return findOrders(orderQuery, null);
    }

    @Override
    public Iterable<Order> findOrders(OrderQuery query, UUID owner) {
        return orderRepository.findAll(new RestrictedOrderSpecification(query, owner));
    }

    @Override
    public Iterable<Order> findOrders(AllOrdersQuery query, String... order) {
        Sort sort = null;
        for (var o : order)
            sort = Sort.by(o);

        var spec = new AllOrdersSpecification(query);
        return sort == null ? orderRepository.findAll(spec) : orderRepository.findAll(spec, sort);
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

    private void sendNotification(OrderNotification.Type type, Order order, UUID customer) {
        try {
            notificationService.createNotification(new OrderNotification()
                    .withRecipients(order.getTeam().getEmployees())
                    .withType(type)
                    .withPayload(new OrderNotificationPayload(order.getId(), customer)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
