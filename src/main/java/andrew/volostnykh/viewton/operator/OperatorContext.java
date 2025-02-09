package andrew.volostnykh.viewton.operator;

import java.util.ArrayList;
import java.util.List;

public class OperatorContext {

    private static final List<Operator> DEFAULT_OPERATORS = new ArrayList<Operator>();

    static {
        DEFAULT_OPERATORS.add(new LessOrEqualsOperator());
        DEFAULT_OPERATORS.add(new GreaterOrEqualOperator());
        DEFAULT_OPERATORS.add(new NotEqualOperator());
        DEFAULT_OPERATORS.add(new LessOperator());
        DEFAULT_OPERATORS.add(new GreaterOperator());
        DEFAULT_OPERATORS.add(new RangeOperator());
        DEFAULT_OPERATORS.add(new OrOperator());
        DEFAULT_OPERATORS.add(new EqualOperator());
    }

    public static Operator findApplicableOperator(String condition) {
        return DEFAULT_OPERATORS.stream()
                .filter(operator -> operator.contains(condition))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to parse condition: unknown operator specified. Condition: " + condition)
                );
    }
}
