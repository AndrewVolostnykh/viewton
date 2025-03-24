package com.viewton;

import java.util.Map;

/**
 * A utility class that builds a {@link ViewtonQuery} instance from the given request parameters.
 * The `ViewtonQueryMapper` uses predefined mappers to parse the request parameters and
 * generate a fully populated {@link ViewtonQuery} object.
 *
 * <p>The `of()` method is the main method, which takes the request parameters (typically from
 * an HTTP request) and a default page size to construct the `ViewtonQuery` instance. The method
 * applies a series of mappers defined in {@link ViewtonMappersContext} to extract necessary
 * values from the request and populate the corresponding fields of the `ViewtonQuery`.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Map<String, String> requestParams = new HashMap<>();
 * requestParams.put("page", "1");
 * requestParams.put("page_size", "10");
 * requestParams.put("attributes", "name,age");
 * requestParams.put("sorting", "-name");
 * ViewtonQuery query = ViewtonQueryMapper.of(requestParams, 20);
 * </pre>
 *
 * <p>This will create a `ViewtonQuery` with pagination, sorting, and selected attributes based on
 * the provided request parameters.</p>
 */
public class ViewtonQueryMapper {
    public static ViewtonQuery of(Map<String, String> requestParams, int defaultPageSize) {
        return ViewtonQuery.builder()
                .rawWhereClauses(ViewtonMappersContext.mapWhereClauses.apply(requestParams))
                .rawOrderByes(ViewtonMappersContext.mapOrderByes.apply(requestParams))
                .page(ViewtonMappersContext.mapPage.apply(requestParams))
                .pageSize(ViewtonMappersContext.mapPageSize.apply(requestParams, defaultPageSize))
                .attributes(ViewtonMappersContext.mapAttributes.apply(requestParams))
                .sum(ViewtonMappersContext.mapSumAttributes.apply(requestParams))
                .distinct(ViewtonMappersContext.isDistinct.apply(requestParams))
                .count(ViewtonMappersContext.isCount.apply(requestParams))
                .concurrentMode(ViewtonMappersContext.isCount.apply(requestParams))
                .build();
    }
}
