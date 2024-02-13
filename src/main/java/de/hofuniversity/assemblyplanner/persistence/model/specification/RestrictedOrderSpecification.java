package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.UUID;

public class RestrictedOrderSpecification extends BaseOrderSpecification {

    private final UUID customerId;

    public RestrictedOrderSpecification(OrderQuery orderQuery, UUID customerId) {
        super(orderQuery);
        this.customerId = customerId;
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var rootPredicate = super.toPredicate(root, query, criteriaBuilder);
        var customer = root.get("customer");
        if(customerId != null)
            rootPredicate = criteriaBuilder.and(rootPredicate, criteriaBuilder.equal(customer.get("id"), customerId));

        return rootPredicate;
    }
}
