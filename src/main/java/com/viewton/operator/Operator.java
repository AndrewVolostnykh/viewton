package com.viewton.operator;

import com.viewton.dto.ComparableValue;
import com.viewton.RawWhereClause;
import com.viewton.type.JavaTypeToComparableResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;

import java.util.List;

/**
 * Abstract base class representing an operator used for querying and filtering in requests.
 * <p>
 * Operators are typically used to perform specific operations (like comparison or range checking)
 * on fields in query parameters. This class encapsulates the operator value and provides common functionality
 * for all operator types.
 * </p>
 */
@Getter
public abstract class Operator {

    private final String value;

    public Operator(String value) {
        this.value = value;
    }

    public boolean contains(String condition) {
        return condition.contains(value);
    }

    public abstract Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb);

    protected List<ComparableValue> valueToComparable(RawWhereClause clause, Path path) {
        return clause.getValues()
                .stream()
                .map((rawValue -> {
                    rawValue.setJavaType(path.getJavaType());
                    return JavaTypeToComparableResolver.toJavaComparable(rawValue);
                }))
                .toList();
    }

    protected ComparableValue firstValueToComparable(RawWhereClause clause, Path path) {
        return this.valueToComparable(clause, path).get(0);
    }
}
