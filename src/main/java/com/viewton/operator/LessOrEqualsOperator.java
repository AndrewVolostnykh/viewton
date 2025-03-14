package com.viewton.operator;

import com.viewton.RawWhereClause;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class LessOrEqualsOperator extends Operator {

    public LessOrEqualsOperator() {
        super("<=");
    }

    /**
     * Converts a "less than or equal to" operator to a {@link Predicate}.
     *
     * @param clause The {@link RawWhereClause} containing the less-than-or-equal condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the less-than-or-equal comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }
}
