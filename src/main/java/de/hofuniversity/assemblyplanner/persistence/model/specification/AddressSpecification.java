package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Address;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AddressQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.UUID;

public class AddressSpecification implements Specification<Address> {

    private final AddressQuery query;
    private final UUID customerId;

    public AddressSpecification(AddressQuery query, UUID customerId) {
        this.query = query;
        this.customerId = customerId;
    }

    public AddressSpecification(AddressQuery query) {
        this(query, null);
    }

    @Override
    public Predicate toPredicate(Root<Address> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if(this.customerId != null) {
            predicates.add(criteriaBuilder.equal(
                    root.get("ownerId"),
                    customerId
            ));
        }

        if(this.query.addressSuffix() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("addressSuffix"),
                    "%" + this.query.addressSuffix() + "%"
            ));
        }

        if(this.query.city() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("city"),
                    "%" + this.query.city() + "%"
            ));
        }

        if(this.query.zip() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("zip"),
                    "%" + this.query.zip() + "%"
            ));
        }

        if(this.query.country() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("country"),
                    "%" + this.query.country() + "%"
            ));
        }

        if(this.query.description() != null) {
            predicates.add(criteriaBuilder.like(
                    root.get("description"),
                    "%" + this.query.description() + "%"
            ));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
