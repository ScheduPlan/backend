package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderProductServiceImpl implements de.hofuniversity.assemblyplanner.service.api.OrderProductService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderProductServiceImpl(@Autowired OrderRepository orderRepository, @Autowired ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order addProduct(
            UUID customerId,
            UUID orderId,
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

    @Override
    public Order deleteProduct(UUID customerId, UUID orderId, UUID productId) {
        Order order = orderRepository.findByCustomerId(customerId, orderId).orElseThrow(ResourceNotFoundException::new);
        Product product = order.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        order.getProducts().remove(product);
        return orderRepository.save(order);
    }
}
