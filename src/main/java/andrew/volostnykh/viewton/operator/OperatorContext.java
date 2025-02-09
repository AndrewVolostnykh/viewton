package andrew.volostnykh.viewton.operator;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for determining and managing operators that can be used in query conditions.
 * <p>
 * This class holds a list of default operators and provides a method to find the applicable operator
 * based on a given condition. Operators are typically used in filtering or querying conditions to
 * perform operations like equality checks, comparisons, range checks, and more.
 * </p>
 */
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

    /**
     * Adds new custom operator to the context.
     */
    public static void registerOperator(Operator operator) {
        DEFAULT_OPERATORS.add(operator);
    }

    /**
     * Finds the applicable operator based on the given condition.
     * <p>
     * This method iterates over a list of predefined operators and returns the first operator whose
     * value is found within the provided condition. If no operator matches, an exception is thrown.
     * </p>
     *
     * @param condition The condition string to evaluate, which may contain an operator.
     * @return The operator that matches the condition.
     * @throws IllegalArgumentException If no operator is found that matches the condition.
     */
    public static Operator findApplicableOperator(String condition) {
        return DEFAULT_OPERATORS.stream()
                .filter(operator -> operator.contains(condition))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to parse condition: unknown operator specified. Condition: " + condition)
                );
    }
}
