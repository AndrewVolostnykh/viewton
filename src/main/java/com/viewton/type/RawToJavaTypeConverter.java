package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;

/**
 * Interface for converting raw database values into Java-compatible {@link ComparableValue} objects.
 * <p>
 * Implementations of this interface define how specific database types (represented by raw values) should be
 * converted into Java types that can be used for comparison operations or other logic in the application.
 * </p>
 * <p>
 * Each converter is responsible for checking if it supports a particular Java type and then performing the
 * conversion to a {@link ComparableValue}.
 * </p>
 */
public interface RawToJavaTypeConverter {

    /**
     * Determines if this converter can handle the given Java type.
     * <p>
     * This method is used to check if the converter can process a particular type of database value.
     * </p>
     *
     * @param javaType The Java type to check.
     * @return {@code true} if this converter can handle the specified type, {@code false} otherwise.
     */
    boolean requiredType(Class<?> javaType);

    /**
     * Converts a raw database value into a {@link ComparableValue}.
     * <p>
     * This method performs the actual conversion, transforming a raw database value (e.g., a string or number)
     * into a Java-compatible value that can be used in comparison operations.
     * </p>
     *
     * @param rawValue The raw database value to convert.
     * @return A {@link ComparableValue} representing the converted value.
     */
    ComparableValue convert(RawValue rawValue);
}
