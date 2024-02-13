package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BaseOrderSpecification implements Specification<Order> {

    protected final OrderQuery orderQuery;

    public BaseOrderSpecification(OrderQuery query) {
        this.orderQuery = query;
    }

    private Path<?> getActualRoot(Path<?> root, String field) {
        if(field == null || field.isBlank())
            return root;

        int i = field.indexOf('.');
        if(i == -1)
            return root;

        root = root.get(field.substring(0, i));

        return getActualRoot(root, field.substring(i + 1));
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if(orderQuery.number() != null)
            predicates.add(criteriaBuilder.equal(root.get("number"), orderQuery.number()));
        if(orderQuery.commissionNumber() != null)
            predicates.add(criteriaBuilder.like(root.get("commissionNumber"),"%" + orderQuery.commissionNumber() + "%"));
        if(orderQuery.description() != null)
            predicates.add(criteriaBuilder.like(root.get("description"), "%" + orderQuery.description() + "%"));
        if(orderQuery.states() != null && orderQuery.states().length > 0)
            predicates.add(root.get("state").in((Object[]) orderQuery.states()));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
