package andrew.volostnykh.viewton;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class RawWhereClause {

    private final String fieldName;
    private final List<String> values;
    private Operator operator;
    private boolean ignoreCase;

    public RawWhereClause(String fieldName, String rawCondition) {
        this.ignoreCase = !fieldName.startsWith("^");
        this.fieldName = fieldName.replace("^", "");
        this.values = Arrays.stream(Operator.values())
                .filter(operator -> rawCondition.contains(operator.toString()))
                .findFirst()
                .map(operator -> {
                    this.operator = operator;
                    return parseValues(rawCondition, operator);
                })
                .orElseThrow(() -> new IllegalArgumentException("Unable to parse condition: unknown operator specified. Condition: " + rawCondition));
        ;
    }

    public List<String> parseValues(String filterValue, Operator operator) {
        if (operator == Operator.RANGE) {
            return Arrays.asList(filterValue.split("\\.\\."));
        } else if (operator == Operator.OR) {
            return Arrays.asList(filterValue.split("\\|"));
        }
        return List.of(filterValue.replaceFirst(filterValue, ""));
    }
}
