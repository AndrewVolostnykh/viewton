package andrew.volostnykh.viewton.operator;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawWhereClause;
import andrew.volostnykh.viewton.operator.common.EqualBasedOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class OrOperator extends EqualBasedOperator {

    public OrOperator() {
        super("|");
    }

    /**
     * Converts an "or" operator to a {@link Predicate} based on {@link EqualBasedOperator#convertEquals(ComparableValue, Path, CriteriaBuilder)}.
     *
     * @param clause The RawWhereClause containing the or condition.
     * @param path   The path to the entity field.
     * @param cb     The CriteriaBuilder used to create the predicate.
     * @return A Criteria API predicate representing the "or" comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.or(
                valueToComparable(clause, path).stream()
                        .map(fieldValue -> convertEquals(fieldValue, path, cb))
                        .toArray(Predicate[]::new)
        );
    }
}
