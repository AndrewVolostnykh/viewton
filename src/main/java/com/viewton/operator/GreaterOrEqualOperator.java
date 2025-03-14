package com.viewton.operator;

import com.viewton.RawWhereClause;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class GreaterOrEqualOperator extends Operator {

    public GreaterOrEqualOperator() {
        super(">=");
    }

    /**
     * Converts a "greater than or equal to" operator to a {@link Predicate}.
     *
     * @param clause The {@link RawWhereClause} containing the greater-than-or-equal condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the greater-than-or-equal comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }
}
