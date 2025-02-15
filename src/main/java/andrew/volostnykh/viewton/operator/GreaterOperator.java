package andrew.volostnykh.viewton.operator;

import andrew.volostnykh.viewton.RawWhereClause;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class GreaterOperator extends Operator {
    public GreaterOperator() {
        super(">");
    }

    /**
     * Converts a "greater than" operator to a {@link Predicate}.
     *
     * @param clause The {@link RawWhereClause} containing the greater-than condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the greater-than comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.greaterThan(path, firstValueToComparable(clause, path).getValue());
    }
}
