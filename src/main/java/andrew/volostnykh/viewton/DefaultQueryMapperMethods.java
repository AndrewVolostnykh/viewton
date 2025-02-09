package andrew.volostnykh.viewton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultQueryMapperMethods {
    public static final String ATTRIBUTES_SEPARATOR = ",";
    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "page_size";
    public static final String SORTING_FIELD = "sorting";
    public static final String ATTRIBUTES = "attributes";
    public static final String TOTAL_ATTRIBUTES = "totalAttributes";
    public static final String DISTINCT = "distinct";
    public static final String COUNT = "count";
    public static final String TOTAL = "total";
    public static final String FIRST_PAGE = "1";

    private static final Set<String> PREDEFINED_ATTRIBUTES = Set.of(
            PAGE,
            PAGE_SIZE,
            SORTING_FIELD,
            ATTRIBUTES,
            DISTINCT,
            COUNT,
            TOTAL,
            TOTAL_ATTRIBUTES
    );

    public static List<RawWhereClause> mapRawWhereClauses(Map<String, String> requestParams) {
        return requestParams.entrySet()
                .stream()
                .filter(entry -> !PREDEFINED_ATTRIBUTES.contains(entry.getKey()))
                .map(entry -> new RawWhereClause(entry.getKey(), entry.getValue()))
                .toList();
    }

    public static int mapPage(Map<String, String> requestParams) {
        return Integer.parseInt(requestParams.getOrDefault(PAGE, FIRST_PAGE));
    }

    public static int mapPageSize(Map<String, String> requestParams, int defaultPageSize) {
        if (defaultPageSize == -1) {
            defaultPageSize = Integer.MAX_VALUE;
        }
        return Integer.parseInt(requestParams.getOrDefault(PAGE_SIZE, Integer.toString(defaultPageSize)));
    }

    public static List<RawOrderBy> mapOrderByes(Map<String, String> params) {
        return Optional.ofNullable(params.get(SORTING_FIELD))
                .map(rawOrderExpression -> Arrays.asList(rawOrderExpression.split(ATTRIBUTES_SEPARATOR)))
                .orElse(new ArrayList<>())
                .stream()
                .map(expression -> {
                    Order order = expression.startsWith("-") ? Order.ASCENDING : Order.DESCENDING;
                    return new RawOrderBy(expression.replace("-", ""), order);
                })
                .toList();
    }

    public static List<String> mapAttributes(Map<String, String> requestParams) {
        return mapAttributes(requestParams, ATTRIBUTES);
    }

    public static List<String> mapTotalAttributes(Map<String, String> requestParams) {
        return mapAttributes(requestParams, TOTAL_ATTRIBUTES);
    }

    private static List<String> mapAttributes(Map<String, String> requestParams, String attributeType) {
        return Optional.ofNullable(requestParams.get(attributeType))
                .map(attributes -> Arrays.asList(attributes.split(ATTRIBUTES_SEPARATOR)))
                .orElse(null);
    }

    public static boolean isDistinct(Map<String, String> requestParams) {
        return requestParams.containsKey(DISTINCT);
    }

    public static boolean isCount(Map<String, String> requestParams) {
        return requestParams.containsKey(COUNT);
    }

    public static boolean isTotal(Map<String, String> requestParams) {
        return requestParams.containsKey(TOTAL);
    }
}
