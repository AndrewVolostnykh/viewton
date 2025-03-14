package com.viewton.operator;

import com.viewton.ComparableValue;
import com.viewton.RawWhereClause;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class RangeOperator extends Operator {

    public RangeOperator() {
        super("..");
    }

    /**
     * Converts a range operator to a {@link Predicate}.
     *
     * @param clause The {@link RawWhereClause} containing the range condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the range comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        List<ComparableValue> pair = valueToComparable(clause, path);
        if (pair.size() != 2) {
            throw new IllegalArgumentException("Invalid range clause: " + clause);
        }

        return cb.between(path, pair.get(0).getValue(), pair.get(1).getValue());
    }
}
