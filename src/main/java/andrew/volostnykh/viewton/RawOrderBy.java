package andrew.volostnykh.viewton;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawOrderBy {
    private String fieldName;
    private Order order;

    public boolean isAscending() {
        return order == Order.ASCENDING;
    }
}
