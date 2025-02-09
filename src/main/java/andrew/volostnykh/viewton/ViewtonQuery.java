package andrew.volostnykh.viewton;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ViewtonQuery {
    private List<? extends RawWhereClause> rawWhereClauses;
    private List<RawOrderBy> rawOrderByes;
    private List<String> attributes;
    private List<String> totalAttributes;
    private int pageSize;
    private int page;
    private boolean count;
    private boolean distinct;
    private boolean total;

    public boolean doNotCount() {
        return !count;
    }

    public boolean doNotTotals() {
        return !total;
    }

    // nature SQL offset
    public int getPage() {
        return (page - 1) * pageSize;
    }
}
