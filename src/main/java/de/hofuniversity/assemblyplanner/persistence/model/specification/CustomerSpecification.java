package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.Customer;
import de.hofuniversity.assemblyplanner.persistence.model.dto.CustomerQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class CustomerSpecification implements Specification<Customer> {
    private final CustomerQuery query;
    public CustomerSpecification(CustomerQuery query) {
        this.query = query;
    }

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if(this.query.customerNumber() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("customerNumber").as(String.class),
                    "%" + this.query.customerNumber() + "%"
            ));
        }

        if(this.query.company() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("company"),
                    "%" + this.query.company() + "%"
            ));
        }

        if(this.query.firstName() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("firstName"),
                    "%" + this.query.firstName() + "%"
            ));
        }

        if(this.query.lastName() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("lastName"),
                    "%" + this.query.lastName() + "%"
            ));
        }

        if(this.query.addressQuery() != null) {
            var addressSpecification = new AddressSpecification(this.query.addressQuery());
            var sub = query.subquery(Address.class);
            var subRoot = sub.from(Address.class);
            sub = sub.select(subRoot.get("id"))
                    .where(
                        addressSpecification.toPredicate(subRoot, query, criteriaBuilder),
                        criteriaBuilder.equal(subRoot.get("ownerId"), root.get("id"))
                    );
            predicates.add(criteriaBuilder.exists(sub));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
