package com.viewton;

import com.viewton.operator.EqualOperator;
import com.viewton.operator.GreaterOperator;
import com.viewton.operator.GreaterOrEqualOperator;
import com.viewton.operator.LessOperator;
import com.viewton.operator.LessOrEqualsOperator;
import com.viewton.operator.NotEqualOperator;
import com.viewton.operator.OrOperator;
import com.viewton.operator.RangeOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * A utility class that converts a list of {@link RawWhereClause} objects to a list of {@link Predicate} objects
 * for use with the Criteria API in Java Persistence Query Language (JPQL).
 *
 * <p>The `WherePredicatesConverter` class provides methods to register and convert different types of where clauses
 * (e.g., equality, inequality, range, and logical operators) into `Predicate` objects that can be used to build
 * a SQL query dynamically using the Criteria API.</p>
 *
 * <p>The class supports various operators, including:
 * <ul>
 *   <li>{@link LessOrEqualsOperator}</li>
 *   <li>{@link GreaterOrEqualOperator}</li>
 *   <li>{@link LessOperator}</li>
 *   <li>{@link GreaterOperator}</li>
 *   <li>{@link NotEqualOperator}</li>
 *   <li>{@link OrOperator}</li>
 *   <li>{@link RangeOperator}</li>
 *   <li>{@link EqualOperator}</li>
 * </ul>
 * </p>
 *
 * <p>This allows you to dynamically build JPQL queries based on the parsed where clauses.</p>
 */
public class WherePredicatesConverter {

    /**
     * Converts a list of {@link RawWhereClause} objects into a list of Criteria API {@link Predicate} objects
     * based on the provided root and criteria builder.
     *
     * @param whereClauses The list of raw where clauses to be converted into predicates.
     * @param root         The root of the entity being queried.
     * @param cb           The CriteriaBuilder used to build the predicates.
     * @return A list of Criteria API predicates.
     */
    public static List<Predicate> convert(
            List<? extends RawWhereClause> whereClauses,
            Root root,
            CriteriaBuilder cb
    ) {
        return whereClauses.stream()
                .map(clause -> clause.getOperator().toPredicate(clause, root.get(clause.getFieldName()), cb))
                .toList();
    }

}