package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AllOrdersQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class AllOrdersSpecification extends RestrictedOrderSpecification {

    private final AllOrdersQuery ordersQuery;
    public AllOrdersSpecification(AllOrdersQuery orderQuery) {
        super(new OrderQuery(orderQuery.orderNumber(), orderQuery.commissionNumber(), orderQuery.states(), orderQuery.description()), orderQuery.customerId());
        this.ordersQuery = orderQuery;
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var rootPredicate = super.toPredicate(root, query, criteriaBuilder);

        var customer = root.get("customer");

        if (ordersQuery.companyName() != null) {
            rootPredicate = criteriaBuilder.and(
                    rootPredicate,
                    criteriaBuilder.like(customer.get("company"), "%" + ordersQuery.companyName() + "%")
            );
        }

        if (ordersQuery.customerNumber() != null) {
            rootPredicate = criteriaBuilder.and(
                    rootPredicate,
                    criteriaBuilder.like(customer.get("customerNumber"), "%" + ordersQuery.customerNumber() + "%")
            );
        }

        if(ordersQuery.teamId() != null){
            rootPredicate = criteriaBuilder.and(
                    rootPredicate, criteriaBuilder.equal(root.get("team").get("id"), ordersQuery.teamId())
            );
        }

        return rootPredicate;
    }
}
