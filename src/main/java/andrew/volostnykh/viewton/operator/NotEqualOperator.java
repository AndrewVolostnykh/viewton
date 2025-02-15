package andrew.volostnykh.viewton.operator;

import andrew.volostnykh.viewton.RawWhereClause;
import andrew.volostnykh.viewton.operator.common.EqualBasedOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class NotEqualOperator extends EqualBasedOperator {

    public NotEqualOperator() {
        super("<>");
    }


    @Override
    public Predicate toPredicate(RawWhereClause clause, Path path, CriteriaBuilder cb) {
        return cb.not(convertEquals(firstValueToComparable(clause, path), path, cb));
    }
}
