package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.PersonQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PersonSpecification implements Specification<Person> {
    private PersonQuery personQuery;

    public PersonSpecification(PersonQuery personQuery) {
        this.personQuery = personQuery;
    }

    @Override
    public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (this.personQuery.firstName() != null) {
            predicates.add(
                    criteriaBuilder.like(root.get("firstName"), "%" + this.personQuery.firstName() + "%")
            );
        }
        if (this.personQuery.lastName() != null) {
            predicates.add(
                    criteriaBuilder.like(root.get("lastName"), "%" + this.personQuery.lastName() + "%")
            );
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
