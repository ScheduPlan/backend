package de.hofuniversity.assemblyplanner.persistence.model.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.Date;

public class DateSpecification<T> implements Specification<T> {

    private final Date startDate;
    private final Date endDate;
    private final String field;

    public DateSpecification(String field, Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.field = field;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;
        if(startDate != null && endDate != null)
            predicate = criteriaBuilder.between(root.get(field), startDate, endDate);
        else if(endDate != null)
            predicate = criteriaBuilder.lessThan(root.get(field), endDate);
        else if(startDate != null)
            predicate = criteriaBuilder.greaterThan(root.get(field), startDate);
        return predicate;
    }
}
