package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Person;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeQuery;
import de.hofuniversity.assemblyplanner.persistence.model.dto.PersonQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class EmployeeSpecification implements Specification<Employee> {
    private EmployeeQuery employeeQuery;

    public EmployeeSpecification(EmployeeQuery employeeQuery) {
        this.employeeQuery = employeeQuery;
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if(employeeQuery.role() != null) {
            predicates.add(
                    criteriaBuilder.equal(root.get("role"), employeeQuery.role())
            );
        }

        if(employeeQuery.unassigned() != null && employeeQuery.unassigned()) {
            predicates.add(
                    criteriaBuilder.isNotNull(root.get("team"))
            );
        }

        if(employeeQuery.firstName() != null || employeeQuery.lastName() != null) {
            var subRoot = query.from(Person.class);
            var pQuery = new PersonQuery(employeeQuery.firstName(), employeeQuery.lastName());
            predicates.add(new PersonSpecification(pQuery)
                    .toPredicate(subRoot, query, criteriaBuilder));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
