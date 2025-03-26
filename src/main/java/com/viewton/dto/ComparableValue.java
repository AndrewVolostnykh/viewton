package com.viewton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a value that can be compared, typically extracted from a "where" clause in a query.
 * This class encapsulates a {@link Comparable} value, along with a flag indicating whether
 * the comparison should be case-insensitive. It is used to store values in a format that is compatible
 * with the type of the entity field, allowing for correct comparison in SQL queries.
 */
@Getter
@AllArgsConstructor
public class ComparableValue {
    private Comparable value;
    private boolean ignoreCase;
}
