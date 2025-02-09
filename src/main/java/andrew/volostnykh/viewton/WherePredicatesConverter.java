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

public class WherePredicatesConverter {

    private static final Map<Class<? extends Operator>, ThreeArgsFunction<RawWhereClause, Path, CriteriaBuilder, Predicate>> predicateConverters = new HashMap<>();

    static {
        registerConverter(LessOrEqualsOperator.class, WherePredicatesConverter::convertLessOrEquals);
        registerConverter(GreaterOrEqualOperator.class, WherePredicatesConverter::convertGreaterOrEquals);
        registerConverter(LessOperator.class, WherePredicatesConverter::convertLess);
        registerConverter(GreaterOperator.class, WherePredicatesConverter::convertGreater);
        registerConverter(NotEqualOperator.class, WherePredicatesConverter::convertNotEquals);
        registerConverter(OrOperator.class, WherePredicatesConverter::convertOr);
        registerConverter(RangeOperator.class, WherePredicatesConverter::convertRange);
        registerConverter(EqualOperator.class, WherePredicatesConverter::convertNotEquals);
    }

    public static void registerConverter(
            Class<? extends Operator> operator,
            ThreeArgsFunction<RawWhereClause, Path, CriteriaBuilder, Predicate> converter) {
        predicateConverters.put(operator, converter);
    }

    public static List<Predicate> convert(List<? extends RawWhereClause> whereClauses, Root root, CriteriaBuilder cb) {
        return whereClauses.stream()
                .map(clause -> predicateConverters.get(clause.getOperator().getClass()).apply(
                        clause,
                        root.get(clause.getFieldName()),
                        cb)
                )
                .toList();
    }

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

    public static Predicate convertEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        ComparableValue value = valueToComparable(clause, path).get(0);

        return convertEquals(value, path, cb);
    }

    protected static Predicate convertNotEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.not(convertEquals(clause, path, cb));
    }

    protected static Predicate convertLess(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThan(path, valueToComparable(clause, path).get(0).getValue());
    }

    protected static Predicate convertGreater(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThan(path, firstValueToComparable(clause, path).getValue());
    }

    protected static Predicate convertLessOrEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    protected static Predicate convertGreaterOrEquals(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    protected static Predicate convertRange(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        List<ComparableValue> pair = valueToComparable(clause, path);
        if (pair.size() != 2) {
            throw new IllegalArgumentException("Invalid range clause: " + clause);
        }

        return cb.between(path, pair.get(0).getValue(), pair.get(1).getValue());
    }

    protected static Predicate convertOr(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.or(
                valueToComparable(clause, path).stream()
                        .map(fieldValue -> convertEquals(fieldValue, path, cb))
                        .toArray(Predicate[]::new)
        );
    }

    protected static ComparableValue firstValueToComparable(RawWhereClause clauses, Path path) {
        return valueToComparable(clauses, path).get(0);
    }

    protected static List<ComparableValue> valueToComparable(RawWhereClause clause, Path path) {
        return clause.getValues()
                .stream()
                .map(value -> valueToComparable(value, path))
                .toList();
    }

    protected static ComparableValue valueToComparable(RawValue rawValue, Path path) {
        rawValue.setJavaType(path.getJavaType());
        return JavaTypeToComparableResolver.toJavaComparable(rawValue);
    }
}
