package com.viewton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation for mapping query parameters from a request (e.g., URL parameters)
 * into the corresponding Java objects that are used to build SQL queries using the JPA Criteria API.
 *
 * <p>This class provides static methods to map common query parameters such as filters, sorting,
 * pagination, and attributes, to their appropriate objects. It helps in transforming query
 * parameters (like 'page', 'count', 'attributes', etc.) into criteria objects for the query.</p>
 *
 * <p>The default behavior can be customized by modifying the mappers in the {@link ViewtonMappersContext} class.</p>
 *
 * <p>Example of how the methods can be used:</p>
 * <pre>
 * Map<String, String> queryParams = ...;
 * List<RawWhereClause> whereClauses = DefaultQueryMapperMethods.mapRawWhereClauses(queryParams);
 * List<RawOrderBy> orderBy = DefaultQueryMapperMethods.mapOrderByes(queryParams);
 * </pre>
 *
 * <p>Expected mappers behavior</p>
 * <pre>
 * {@code GET /api/entities?sum=<>1000&total=true&page=1&page_size=20&attributes=name,age&sorting=-name}
 * </p>this query will be parsed into:
 * {@code rawWhereClauses: Contains the condition where sum != 1000.
 * rawOrderByes: Contains sorting by the name field in descending order.
 * page: Set to 1 (indicating the first page).
 * pageSize: Set to 20.
 * attributes: Set to name and age.
 * totalAttributes: Set to total=true (enabling total calculation).
 * distinct: If the request contains distinct, it will be true.
 * count: If the request contains count, it will be true.
 * total: Set to true.}
 * </pre>
 */
public class DefaultQueryMapperMethods {
    public static final String ATTRIBUTES_SEPARATOR = ",";
    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "page_size";
    public static final String SORTING_FIELD = "sorting";
    public static final String ATTRIBUTES = "attributes";
    public static final String SUM_ATTRIBUTES = "sum";
    public static final String DISTINCT = "distinct";
    public static final String COUNT = "count";
    public static final String FIRST_PAGE = "1";

    private static final Set<String> PREDEFINED_ATTRIBUTES = Set.of(
            PAGE,
            PAGE_SIZE,
            SORTING_FIELD,
            ATTRIBUTES,
            DISTINCT,
            COUNT,
            SUM_ATTRIBUTES
    );

    /**
     * Maps the query parameters to a list of raw where clauses.
     * Filters out predefined attributes and converts the remaining entries into raw where clauses.
     *
     * @param requestParams the map of query parameters.
     * @return a list of {@link RawWhereClause} based on the query parameters.
     */
    public static List<RawWhereClause> mapRawWhereClauses(Map<String, String> requestParams) {
        return requestParams.entrySet()
                .stream()
                .filter(entry -> !PREDEFINED_ATTRIBUTES.contains(entry.getKey()))
                .map(entry -> RawWhereClauseInstance.instantiate.apply(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Maps the query parameters to the page number. Defaults to 1 if not provided.
     *
     * @param requestParams the map of query parameters.
     * @return the page number as an integer.
     */
    public static int mapPage(Map<String, String> requestParams) {
        return Integer.parseInt(requestParams.getOrDefault(PAGE, FIRST_PAGE));
    }

    /**
     * Maps the query parameters to the page size. Uses the provided default value if not specified.
     *
     * @param requestParams   the map of query parameters.
     * @param defaultPageSize the default page size if not provided.
     * @return the page size as an integer.
     */
    public static int mapPageSize(Map<String, String> requestParams, int defaultPageSize) {
        if (defaultPageSize == -1) {
            defaultPageSize = Integer.MAX_VALUE;
        }
        return Integer.parseInt(requestParams.getOrDefault(PAGE_SIZE, Integer.toString(defaultPageSize)));
    }

    /**
     * Maps the query parameters to a list of raw order by clauses.
     *
     * @param params the map of query parameters.
     * @return a list of {@link RawOrderBy} based on the sorting field parameters.
     */
    public static List<RawOrderBy> mapOrderByes(Map<String, String> params) {
        return Optional.ofNullable(params.get(SORTING_FIELD))
                .map(rawOrderExpression -> Arrays.asList(rawOrderExpression.split(ATTRIBUTES_SEPARATOR)))
                .orElse(new ArrayList<>())
                .stream()
                .map(expression -> {
                    Order order = expression.startsWith("-") ? Order.DESCENDING : Order.ASCENDING;
                    return new RawOrderBy(expression.replace("-", ""), order);
                })
                .toList();
    }

    /**
     * Maps the query parameters to a list of attributes.
     *
     * @param requestParams the map of query parameters.
     * @return a list of attributes to be selected.
     */
    public static List<String> mapAttributes(Map<String, String> requestParams) {
        return mapAttributes(requestParams, ATTRIBUTES);
    }

    /**
     * Maps the query parameters to a list of total attributes.
     *
     * @param requestParams the map of query parameters.
     * @return a list of total attributes.
     */
    public static List<String> mapSumAttributes(Map<String, String> requestParams) {
        return mapAttributes(requestParams, SUM_ATTRIBUTES);
    }

    /**
     * Maps the query parameters to a list of attributes (can be used for either regular or total attributes).
     *
     * @param requestParams the map of query parameters.
     * @param attributeType the type of attribute to map (either "attributes" or "totalAttributes").
     * @return a list of attributes based on the query parameters.
     */
    private static List<String> mapAttributes(Map<String, String> requestParams, String attributeType) {
        return Optional.ofNullable(requestParams.get(attributeType))
                .map(attributes -> Arrays.asList(attributes.split(ATTRIBUTES_SEPARATOR)))
                .orElse(null);
    }

    /**
     * Determines if the query parameters indicate that distinct results are required.
     *
     * @param requestParams the map of query parameters.
     * @return {@code true} if distinct results are requested, otherwise {@code false}.
     */
    public static boolean isDistinct(Map<String, String> requestParams) {
        return requestParams.containsKey(DISTINCT);
    }

    /**
     * Determines if the query parameters indicate that the query should count the results.
     *
     * @param requestParams the map of query parameters.
     * @return {@code true} if count is requested, otherwise {@code false}.
     */
    public static boolean isCount(Map<String, String> requestParams) {
        return requestParams.containsKey(COUNT);
    }

    /**
     * Determines if the query parameters indicate that total information is required.
     *
     * @param requestParams the map of query parameters.
     * @return {@code true} if total is requested, otherwise {@code false}.
     */
    public static boolean isSum(Map<String, String> requestParams) {
        return requestParams.containsKey(SUM_ATTRIBUTES) && requestParams.get(SUM_ATTRIBUTES) != null;
    }
}
