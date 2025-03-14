package com.viewton;

import lombok.Data;

/**
 * This class represents a parsed value extracted from a "where" clause in the request parameters.
 * It stores the value to be compared, a flag indicating if the comparison should ignore case,
 * and the Java type of the value (for type-specific comparisons).
 *
 * <p>Used by the {@link RawWhereClause} class to store the individual values that are compared
 * with the field specified in the "where" clause, providing flexibility for various types of operators
 * and condition formats (e.g., equality, range, or pattern matching).</p>
 *
 * <p>Example: For a query parameter like <code>name=^someGuy</code>, the parsed value could be stored as:</p>
 * <ul>
 *   <li>value: "someGuy"</li>
 *   <li>ignoreCase: true</li>
 *   <li>javaType: depends on your entity field type</li>
 * </ul>
 */
@Data
public class RawValue {
    private String value;
    private boolean ignoreCase = false;
    private Class<?> javaType;
}
