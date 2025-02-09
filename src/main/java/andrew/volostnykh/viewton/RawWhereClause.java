package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.operator.Operator;
import andrew.volostnykh.viewton.operator.OperatorContext;
import andrew.volostnykh.viewton.utils.Strings;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class RawWhereClause {

    private final String fieldName;
    private final List<RawValue> values;
    private Operator operator;

    RawWhereClause(String fieldName, String rawCondition) {
        this.fieldName = fieldName;
        this.operator = OperatorContext.findApplicableOperator(rawCondition);
        this.values = parseValues(rawCondition, operator);
    }

    public List<RawValue> parseValues(String filterValue, Operator operator) {
        if ("..".equals(operator.getValue())) {
            return splitToRawValue(filterValue, "\\.\\.");
        } else if ("|".equals(operator.getValue())) {
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
