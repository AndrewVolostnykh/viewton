package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.operator.Operator;
import andrew.volostnykh.viewton.operator.OperatorContext;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a parsed "where" clause from the query parameters in a request.
 * It stores the field name, the operator, and the values for filtering data.
 * The class is responsible for parsing the condition string and identifying the corresponding operator and values.
 *
 * <p>It is typically used to represent filters in a URL query string, where the filter condition can be based on various operators
 * such as "equals", "greater than", "less than", etc. The parsed "where" clause is then used to build a SQL query using the JPA Criteria API.</p>
 *
 * <p>Example: For a query parameter like <code>sum=<>1000</code>, this class would store:
 * <ul>
 *   <li>fieldName: "sum"</li>
 *   <li>operator: {@link andrew.volostnykh.viewton.operator.NotEqualOperator}</li>
 *   <li>values: [1000]</li>
 * </ul>
 * </p>
 */
@Data
public class RawWhereClause {

    /**
     * The field name on which the condition is applied (e.g., "sum", "total")
     */
    private final String fieldName;

    /**
     * The list of values for this condition, to be compared with the field
     */
    private final List<RawValue> values;

    /**
     * The operator for the condition (e.g., equals, greater than, not equals)
     */
    private Operator operator;

    /**
     * Constructs a new {@link RawWhereClause} by parsing the provided condition string.
     *
     * @param fieldName    the name of the field being filtered (e.g., "sum", "total")
     * @param rawCondition the raw condition string (e.g., "<>1000", "=1000", "|value1|value2")
     */
    RawWhereClause(String fieldName, String rawCondition) {
        this.fieldName = fieldName;
        this.operator = OperatorContext.findApplicableOperator(rawCondition);
        this.values = parseValues(rawCondition, operator);
    }

    /**
     * Parses the filter values based on the operator.
     *
     * <p>If the operator is a range ("..") or a pipe ("|"), the condition string is split accordingly.
     * Otherwise, the value is extracted and stored in a {@link RawValue} object, with case-sensitivity handling
     * if the value contains the "^" character.</p>
     *
     * @param filterValue the raw filter condition (e.g., "1000", "value1|value2", "^abc")
     * @param operator    the operator for the condition (e.g., "=", "<>", "|", "..")
     * @return a list of {@link RawValue} objects representing the parsed values.
     */
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

    /**
     * Splits the filter value string based on a regular expression and creates a list of {@link RawValue} objects.
     *
     * @param filterValue the string to be split into values (e.g., "value1|value2|value3")
     * @param splitRegex  the regular expression used to split the string (e.g., "\\." or "\\|")
     * @return a list of {@link RawValue} objects representing the individual values.
     */
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