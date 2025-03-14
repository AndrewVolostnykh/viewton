package com.viewton.operator.common;

import com.viewton.ComparableValue;
import com.viewton.operator.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public abstract class EqualBasedOperator extends Operator {
    public EqualBasedOperator(String value) {
        super(value);
    }

    /**
     * Basic method which prepare Criteria API's equals {@link Predicate}.
     *
     * @param comparableValue The {@link ComparableValue} value to compare.
     * @param path            The {@link Path} to the entity field.
     * @param cb              The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the equality comparison.
     */
    protected Predicate convertEquals(ComparableValue comparableValue, Path path, CriteriaBuilder cb) {
        Comparable value = comparableValue.getValue();

        if ("null".equals(value)) {
            return cb.isNull(path);
        }

        Class javaType = path.getJavaType();
        if (String.class.isAssignableFrom(javaType)) {
            if (comparableValue.isIgnoreCase()) {
                return cb.like(path, value.toString());
            } else {
                return cb.like(cb.lower(path), value.toString().toLowerCase());
            }
        }

        return cb.equal(path, value);
    }

}
