package andrew.volostnykh.viewton.operator;

import andrew.volostnykh.viewton.RawWhereClause;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class LessOperator extends Operator {

    public LessOperator() {
        super("<");
    }

    /**
     * Converts a "less than" operator to a {@link Predicate}.
     *
     * @param clause The {@link RawWhereClause} containing the less-than condition.
     * @param path   The {@link Path} to the entity field.
     * @param cb     The {@link CriteriaBuilder} used to create the predicate.
     * @return A Criteria API predicate representing the less-than comparison.
     */
    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.lessThan(path, firstValueToComparable(clause, path).getValue());
    }
}
