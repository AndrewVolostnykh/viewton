package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.operator.EqualOperator;
import andrew.volostnykh.viewton.operator.GreaterOperator;
import andrew.volostnykh.viewton.operator.GreaterOrEqualOperator;
import andrew.volostnykh.viewton.operator.LessOperator;
import andrew.volostnykh.viewton.operator.LessOrEqualsOperator;
import andrew.volostnykh.viewton.operator.NotEqualOperator;
import andrew.volostnykh.viewton.operator.Operator;
import andrew.volostnykh.viewton.operator.OrOperator;
import andrew.volostnykh.viewton.operator.RangeOperator;
import andrew.volostnykh.viewton.type.JavaTypeToComparableResolver;
import andrew.volostnykh.viewton.utils.ThreeArgsFunction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * A map of operator classes to their corresponding predicate converter functions.
     * This allows for dynamically converting different operators in where clauses to Criteria API predicates.
     */
    private static final Map<Class<? extends Operator>, ThreeArgsFunction<RawWhereClause, Path, CriteriaBuilder, Predicate>> predicateConverters = new HashMap<>();

    static {
        // Registering the various operator types and their corresponding conversion methods
        registerConverter(LessOrEqualsOperator.class, WherePredicatesConverter::convertLessOrEquals);
        registerConverter(GreaterOrEqualOperator.class, WherePredicatesConverter::convertGreaterOrEquals);
        registerConverter(LessOperator.class, WherePredicatesConverter::convertLess);
        registerConverter(GreaterOperator.class, WherePredicatesConverter::convertGreater);
        registerConverter(NotEqualOperator.class, WherePredicatesConverter::convertNotEquals);
        registerConverter(OrOperator.class, WherePredicatesConverter::convertOr);
        registerConverter(RangeOperator.class, WherePredicatesConverter::convertRange);
        registerConverter(EqualOperator.class, WherePredicatesConverter::convertNotEquals);
    }

    /**
     * Registers a new operator type and its corresponding converter function.
     *
     * @param operator The operator class.
     * @param converter The function to convert the operator to a Predicate.
     */
    public static void registerConverter(
            Class<? extends Operator> operator,
            ThreeArgsFunction<RawWhereClause, Path, CriteriaBuilder, Predicate> converter) {
        predicateConverters.put(operator, converter);
    }

    /**
     * Converts a list of {@link RawWhereClause} objects into a list of Criteria API {@link Predicate} objects
     * based on the provided root and criteria builder.
     *
     * @param whereClauses The list of raw where clauses to be converted into predicates.
     * @param root The root of the entity being queried.
     * @param cb The CriteriaBuilder used to build the predicates.
     * @return A list of Criteria API predicates.
     */
    public static List<Predicate> convert(List<? extends RawWhereClause> whereClauses, Root root, CriteriaBuilder cb) {
        return whereClauses.stream()
                .map(clause -> predicateConverters.get(clause.getOperator().getClass()).apply(
                        clause,
                        root.get(clause.getFieldName()),
                        cb)
                )
                .toList();
    }

    /**
     * Converts an equality operator to a {@link Predicate}.
     *
     * @param comparableValue The value to compare.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the equality comparison.
     */
    protected static Predicate convertEquals(ComparableValue comparableValue, Path path, CriteriaBuilder cb) {
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

    /**
     * Converts a RawWhereClause with equality operator to a Predicate.
     *
     * @param clause The RawWhereClause containing the equality condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the equality comparison.
     */
    public static Predicate convertEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        ComparableValue value = valueToComparable(clause, path).get(0);

        return convertEquals(value, path, cb);
    }

    /**
     * Converts a "not equal" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the not-equal condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the not-equal comparison.
     */
    protected static Predicate convertNotEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.not(convertEquals(clause, path, cb));
    }

    /**
     * Converts a "less than" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the less-than condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the less-than comparison.
     */
    protected static Predicate convertLess(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThan(path, valueToComparable(clause, path).get(0).getValue());
    }

    /**
     * Converts a "greater than" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the greater-than condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the greater-than comparison.
     */
    protected static Predicate convertGreater(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThan(path, firstValueToComparable(clause, path).getValue());
    }

    /**
     * Converts a "less than or equal to" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the less-than-or-equal condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the less-than-or-equal comparison.
     */
    protected static Predicate convertLessOrEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    /**
     * Converts a "greater than or equal to" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the greater-than-or-equal condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the greater-than-or-equal comparison.
     */
    protected static Predicate convertGreaterOrEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    /**
     * Converts a range operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the range condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the range comparison.
     */
    protected static Predicate convertRange(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        List<ComparableValue> pair = valueToComparable(clause, path);
        if (pair.size() != 2) {
            throw new IllegalArgumentException("Invalid range clause: " + clause);
        }

        return cb.between(path, pair.get(0).getValue(), pair.get(1).getValue());
    }

    /**
     * Converts an "or" operator to a {@link Predicate}.
     *
     * @param clause The RawWhereClause containing the or condition.
     * @param path The path to the entity field.
     * @param cb The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the "or" comparison.
     */
    protected static Predicate convertOr(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.or(
                valueToComparable(clause, path).stream()
                        .map(fieldValue -> convertEquals(fieldValue, path, cb))
                        .toArray(Predicate[]::new)
        );
    }

    /**
     * Converts the first value of a RawWhereClause to a ComparableValue.
     *
     * @param clauses The RawWhereClause to convert.
     * @param path The path to the entity field.
     * @return The first ComparableValue extracted from the clause.
     */
    protected static ComparableValue firstValueToComparable(RawWhereClause clauses, Path path) {
        return valueToComparable(clauses, path).get(0);
    }

    /**
     * Converts all values of a RawWhereClause to a list of ComparableValues.
     *
     * @param clause The RawWhereClause to convert.
     * @param path The path to the entity field.
     * @return A list of ComparableValues extracted from the clause.
     */
    protected static List<ComparableValue> valueToComparable(RawWhereClause clause, Path path) {
        return clause.getValues()
                .stream()
                .map(value -> valueToComparable(value, path))
                .toList();
    }

    /**
     * Converts a RawValue to a ComparableValue.
     *
     * @param rawValue The RawValue to convert.
     * @param path The path to the entity field.
     * @return The ComparableValue for the given RawValue.
     */
    protected static ComparableValue valueToComparable(RawValue rawValue, Path path) {
        rawValue.setJavaType(path.getJavaType());
        return JavaTypeToComparableResolver.toJavaComparable(rawValue);
    }
}