package de.hofuniversity.assemblyplanner.persistence.model.specification;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeQuery;
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
                    criteriaBuilder.equal(root.get("user").get("role"), employeeQuery.role())
            );
        }

        if(employeeQuery.unassigned() != null && employeeQuery.unassigned()) {
            predicates.add(
                    criteriaBuilder.isNotNull(root.get("team"))
            );
        }

        if(employeeQuery.firstName() != null) {
            predicates.add(
                    criteriaBuilder.like(root.get("firstName"), "%" + employeeQuery.firstName() + "%")
            );
        }

        if(employeeQuery.lastName() != null) {
            predicates.add(
                    criteriaBuilder.like(root.get("lastName"), "%" + employeeQuery.lastName() + "%")
            );
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
