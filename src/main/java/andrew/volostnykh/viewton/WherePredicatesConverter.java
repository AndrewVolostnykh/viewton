package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.type.JavaTypeToComparableResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class WherePredicatesConverter {

    protected final Map<Operator, BiFunction<RawWhereClause, Path, Predicate>> predicateConverters;
    protected final CriteriaBuilder cb;
    protected final Root root;

    public WherePredicatesConverter(CriteriaBuilder cb, Root root) {
        this.cb = cb;
        this.root = root;
        this.predicateConverters = Map.of(
                Operator.EQUAL, this::convertEquals,
                Operator.NOT_EQUAL, this::convertNotEquals,
                Operator.OR, this::convertOr,
                Operator.LESS, this::convertLess,
                Operator.GREATER, this::convertGreater,
                Operator.GREATER_OR_EQUAL, this::convertGreaterOrEquals,
                Operator.LESS_OR_EQUAL, this::convertLessOrEquals,
                Operator.RANGE, this::convertRange
        );
    }

    public List<Predicate> convert(List<RawWhereClause> whereClauses) {
        return whereClauses.stream()
                .map(clause -> predicateConverters.get(clause.getOperator()).apply(
                        clause,
                        root.get(clause.getFieldName()))
                )
                .toList();
    }

    protected Predicate convertEquals(ComparableValue comparableValue, Path path) {
        Comparable value = comparableValue.getValue();

        if ("null".equals(value)
                || "%null".equals(value)
                || "%null%".equals(value)
                || "null%".equals(value)) {
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

    protected Predicate convertEquals(RawWhereClause clause, Path path) {
        ComparableValue value = valueToComparable(clause, path).get(0);

        return convertEquals(value, path);
    }

    protected Predicate convertNotEquals(RawWhereClause clause, Path path) {
        return cb.not(convertEquals(clause, path));
    }

    protected Predicate convertLess(RawWhereClause clause, Path path) {
        return cb.lessThan(path, valueToComparable(clause, path).get(0).getValue());
    }

    protected Predicate convertGreater(RawWhereClause clause, Path path) {
        return cb.greaterThan(path, firstValueToComparable(clause, path).getValue());
    }

    protected Predicate convertLessOrEquals(RawWhereClause clause, Path path) {
        return cb.lessThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    protected Predicate convertGreaterOrEquals(RawWhereClause clause, Path path) {
        return cb.greaterThanOrEqualTo(path, firstValueToComparable(clause, path).getValue());
    }

    protected Predicate convertRange(RawWhereClause clause, Path path) {
        List<ComparableValue> pair = valueToComparable(clause, path);
        if (pair.size() != 2) {
            throw new IllegalArgumentException("Invalid range clause: " + clause);
        }

        return cb.between(path, pair.get(0).getValue(), pair.get(1).getValue());
    }

    protected Predicate convertOr(RawWhereClause clause, Path path) {
        return cb.or(
                valueToComparable(clause, path).stream()
                        .map(fieldValue -> convertEquals(fieldValue, path))
                        .toArray(Predicate[]::new)
        );
    }

    protected ComparableValue firstValueToComparable(RawWhereClause clauses, Path path) {
        return valueToComparable(clauses, path).get(0);
    }

    protected List<ComparableValue> valueToComparable(RawWhereClause clause, Path path) {
        return clause.getValues()
                .stream()
                .map(value -> valueToComparable(value, path))
                .toList();
    }

    protected ComparableValue valueToComparable(RawValue rawValue, Path path) {
        rawValue.setJavaType(path.getJavaType());
        return JavaTypeToComparableResolver.toJavaComparable(rawValue);
    }
}
