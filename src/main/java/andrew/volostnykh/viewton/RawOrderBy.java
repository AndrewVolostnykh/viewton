package andrew.volostnykh.viewton;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an ordering condition for sorting query results. This class holds the field name
 * and the sorting order (ascending or descending) for that field. It is typically used to specify
 * how results should be ordered based on request parameters, like in a query string.
 *
 * <p>Example: In the query parameter <code>sorting=fieldName</code>, this class would encapsulate
 * the sorting condition where the results should be ordered by the "fieldName" in descending order.
 * A query parameter like <code>?sorting=-fieldName</code> would represent descending order.</p>
 */
@Getter
@AllArgsConstructor
public class RawOrderBy {
    private String fieldName;
    private Order order;

    public boolean isAscending() {
        return order == Order.ASCENDING;
    }
}
