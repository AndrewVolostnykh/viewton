package com.viewton.operator;

import com.viewton.ComparableValue;
import com.viewton.RawWhereClause;
import com.viewton.operator.common.EqualBasedOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class EqualOperator extends EqualBasedOperator {

    public EqualOperator() {
        super("");
    }

    /**
     * Converts a RawWhereClause with equality operator to a Predicate  based on {@link EqualBasedOperator#convertEquals(ComparableValue, Path, CriteriaBuilder)}.
     *
     * @param clause The {@link RawWhereClause} containing the equality condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the equality comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return convertEquals(firstValueToComparable(clause, path), path, cb);
    }
}
