package andrew.volostnykh.viewton;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class RawWhereClause {

    private final String fieldName;
    private final List<RawValue> values;
    private Operator operator;
    private boolean ignoreCase;

    public RawWhereClause(String fieldName, String rawCondition) {
        this.ignoreCase = fieldName.startsWith("^");
        this.fieldName = fieldName.replace("^", "");
        this.values = Arrays.stream(Operator.values())
                .filter(operator -> rawCondition.contains(operator.getValue()))
                .findFirst()
                .map(operator -> {
                    this.operator = operator;
                    return parseValues(rawCondition, operator);
                })
                .orElseThrow(() -> new IllegalArgumentException("Unable to parse condition: unknown operator specified. Condition: " + rawCondition));
        ;
    }

    public List<RawValue> parseValues(String filterValue, Operator operator) {
        if (operator == Operator.RANGE) {
            return splitToRawValue(filterValue, "\\.\\.");
        } else if (operator == Operator.OR) {
            return splitToRawValue(filterValue, "\\|");
        } else {
            RawValue rawValue = new RawValue();
            if (filterValue.contains("^")) {
                rawValue.setIgnoreCase(true);
                rawValue.setValue(filterValue.replace("^", "").replaceFirst(operator.getValue(), ""));
            } else {
                rawValue.setValue(filterValue.replaceFirst(operator.getValue(), ""));
            }

            return List.of(rawValue);
        }
    }

    public List<RawValue> splitToRawValue(String filterValue, String splitRegex) {
        return Arrays.stream(filterValue.split(splitRegex)).map(value -> {
            RawValue rawValue = new RawValue();
            if (value.contains("^")) {
                rawValue.setValue(value.replace("^", ""));
                rawValue.setIgnoreCase(true);
            } else {
                rawValue.setValue(value);
            }
            return rawValue;
        }).toList();
    }
}
