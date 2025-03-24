package com.viewton;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Represents a fully parsed set of query parameters for building an SQL query.
 * This class encapsulates all the necessary information for constructing an SQL query
 * based on user-supplied request parameters. It holds parsed where clauses, order
 * by clauses, attributes, pagination details, and flags for count and total queries.
 *
 * <p>The class uses the builder pattern, allowing for flexible and convenient construction
 * of a `ViewtonQuery` instance based on query parameters passed from the request.</p>
 *
 * <p>Fields are populated using the data parsed from the request parameters, and the
 * resulting object can be used to create SQL queries using the Criteria API or other
 * methods.</p>
 */
@Data
@Builder
public class ViewtonQuery {
    private List<? extends RawWhereClause> rawWhereClauses;
    private List<RawOrderBy> rawOrderByes;
    private List<String> attributes;
    private List<String> sum;
    private int pageSize;
    private int page;
    private boolean count;
    private boolean distinct;
    private boolean concurrentMode;

    public boolean doNotCount() {
        return !count;
    }

    public boolean doNotSum() {
        return !isSum();
    }
    public boolean isSum() {
        return sum != null && !sum.isEmpty();
    }

    // nature SQL offset
    public int getPage() {
        return (page - 1) * pageSize;
    }
}
