package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.utils.DateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class WherePredicatesConverter {

    private final Map<Operator, BiFunction<RawWhereClause, Path, Predicate>> predicateConverters;
    private final CriteriaBuilder cb;
    private final Root root;

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

    private Predicate convertEquals(RawWhereClause clause, Path path) {
        Comparable value = valueToComparable(clause, path).get(0);

        if ("null".equals(value)
                || "%null".equals(value)
                || "%null%".equals(value)
                || "null%".equals(value)) {
            return cb.isNull(path);
        }

        Class javaType = path.getJavaType();
        if (String.class.isAssignableFrom(javaType)) {
            if (clause.isIgnoreCase()) {
                return cb.like(path, value.toString());
            } else {
                return cb.like(cb.lower(path), value.toString().toLowerCase());
            }
        }

        return cb.equal(path, value);
    }

    private Predicate convertNotEquals(RawWhereClause clause, Path path) {
        return cb.not(convertEquals(clause, path));
    }

    private Predicate convertLess(RawWhereClause clause, Path path) {
        return cb.lessThan(path, valueToComparable(clause, path).get(0));
    }

    private Predicate convertGreater(RawWhereClause clause, Path path) {
        return cb.greaterThan(path, valueToComparable(clause, path).get(0));
    }

    private Predicate convertLessOrEquals(RawWhereClause clause, Path path) {
        return cb.lessThanOrEqualTo(path, valueToComparable(clause, path).get(0));
    }

    private Predicate convertGreaterOrEquals(RawWhereClause clause, Path path) {
        return cb.greaterThanOrEqualTo(path, valueToComparable(clause, path).get(0));
    }

    private Predicate convertRange(RawWhereClause clause, Path path) {
        List<Comparable> pair = valueToComparable(clause, path);
        if (pair.size() != 2) {
            throw new IllegalArgumentException("Invalid range clause: " + clause);
        }

        return cb.between(path, pair.get(0), pair.get(1));
    }

    private Predicate convertOr(RawWhereClause clause, Path path) {
        List<Comparable> values = valueToComparable(clause, path);

        Predicate[] predicates = values.stream()
                .map(fieldValue -> convertEquals(clause, path))
                .toArray(Predicate[]::new);

        return cb.or(predicates);
    }

    public List<Predicate> convert(List<RawWhereClause> whereClauses) {
        return whereClauses.stream()
                .map(clause -> predicateConverters.get(clause.getOperator()).apply(
                        clause,
                        root.get(clause.getFieldName()))
                )
                .toList();
    }

    private List<Comparable> valueToComparable(RawWhereClause clause, Path path) {
        return clause.getValues()
                .stream()
                .map(value -> valueToComparable(value, path))
                .toList();
    }

    private Comparable valueToComparable(String value, Path path) {
        Class<?> javaType = path.getJavaType();
        if (LocalDateTime.class.isAssignableFrom(javaType)) {
            return DateUtil.parseIsoDateTime(value);
        } else if (LocalDate.class.isAssignableFrom(javaType)) {
            return DateUtil.parseIsoDate(value);
        } else if (Integer.class.isAssignableFrom(javaType) || Integer.TYPE == javaType) {
            return Integer.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(javaType) || Boolean.TYPE == javaType) {
            return Boolean.valueOf(value);
        } else if (javaType.isEnum()) {
            return getEnum(javaType, value);
        }
        return value;
    }

    private Comparable getEnum(Class<?> javaType, String fieldValue) {
        return Stream.of(javaType.getEnumConstants())
                .filter(enumValue -> enumValue.toString().equals(fieldValue))
                .map(Comparable.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Undefined enum value: " + fieldValue));
    }

}
