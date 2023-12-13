package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSpecification implements Specification<Order> {

    private final OrderQuery orderQuery;
    private final UUID customerId;

    public OrderSpecification(OrderQuery orderQuery, UUID customerId) {
        this.orderQuery = orderQuery;
        this.customerId = customerId;
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if(orderQuery.number() != null)
            predicates.add(criteriaBuilder.equal(root.get("number"), orderQuery.number()));
        if(orderQuery.commissionNumber() != null)
            predicates.add(criteriaBuilder.equal(root.get("commissionNumber"), orderQuery.commissionNumber()));
        if(orderQuery.description() != null)
            predicates.add(criteriaBuilder.like(root.get("description"), orderQuery.description()));
        if(orderQuery.states() != null && orderQuery.states().length > 0)
            predicates.add(root.get("state").in((Object[]) orderQuery.states()));
        if(customerId != null)
            predicates.add(criteriaBuilder.equal(root.get("customer.id"), customerId));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
