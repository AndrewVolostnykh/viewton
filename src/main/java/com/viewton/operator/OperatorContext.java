package com.viewton.operator;

import com.viewton.lang.NoneThreadSafe;
import lombok.NonNull;

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

    /**
     * The list of registered operators in the library. Order of operators is important
     * because some of them could be similar to another one, so it can be a conflict.
     * For instance if we will set EqualOperator as a first, it will be applied to every parsed conditions,
     * because all of them contain empty string. See {@link OperatorContext#findApplicableOperator(String)}
     */
    private static final List<Operator> OPERATORS = new ArrayList<Operator>();

    static {
        OPERATORS.add(new LessOrEqualsOperator());
        OPERATORS.add(new GreaterOrEqualOperator());
        OPERATORS.add(new NotEqualOperator());
        OPERATORS.add(new LessOperator());
        OPERATORS.add(new GreaterOperator());
        OPERATORS.add(new RangeOperator());
        OPERATORS.add(new OrOperator());
        OPERATORS.add(new EqualOperator());
    }

    /**
     * Adds new custom operator to the context.
     *
     * @param operator the operator to be registered.
     * @param priority the index of operator in list. Represents priority searching operator in list
     */
    @NoneThreadSafe
    public static void registerOperator(@NonNull Operator operator, int priority) {
        OPERATORS.add(priority, operator);
    }

    /**
     * Remove operator from context if it should be replaced or do not need in context of application.
     *
     * @param index index of operator in {@link OperatorContext#OPERATORS}
     */
    @NoneThreadSafe
    public static void removeOperator(int index) {
        OPERATORS.remove(index);
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
        return OPERATORS.stream()
                .filter(operator -> operator.contains(condition))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to parse condition: unknown operator specified. Condition: " + condition)
                );
    }
}
