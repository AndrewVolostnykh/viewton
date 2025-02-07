package andrew.volostnykh.viewton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ViewtonQueryMapper {
    private static final String ATTRIBUTES_SEPARATOR = ",";
    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "page_size";
    public static final String SORTING_FIELD = "sorting";
    private static final String ATTRIBUTES = "attributes";
    private static final String TOTAL_ATTRIBUTES = "totalAttributes";
    private static final String DISTINCT = "distinct";
    private static final String COUNT = "count";
    private static final String TOTAL = "total";
    private static final String FIRST_PAGE = "1";

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

    public static ViewtonQuery of(Map<String, String> requestParams, int defaultPageSize) {
        return ViewtonQuery.builder()
                .rawWhereClauses(mapRawWhereClauses(requestParams))
                .rawOrderByes(getOrderBy(requestParams))
                .page(mapPage(requestParams))
                .pageSize(mapPageSize(requestParams, defaultPageSize))
                .attributes(mapAttributes(requestParams, ATTRIBUTES))
                .totalAttributes(mapAttributes(requestParams, TOTAL_ATTRIBUTES))
                .distinct(containsParam(requestParams, DISTINCT))
                .total(containsParam(requestParams, TOTAL))
                .count(containsParam(requestParams, COUNT))
                .build();
    }

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
        return Integer.parseInt(requestParams.getOrDefault(PAGE_SIZE, Integer.toString(defaultPageSize)));
    }

    public static List<RawOrderBy> getOrderBy(Map<String, String> params) {
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

    public static List<String> mapAttributes(Map<String, String> requestParams, String attributeType) {
        return Optional.ofNullable(requestParams.get(ATTRIBUTES))
                .map(attributes -> Arrays.asList(attributes.split(ATTRIBUTES_SEPARATOR)))
                .orElse(null);
    }

    public static boolean containsParam(Map<String, String> requestParams, String param) {
        return requestParams.containsKey(param);
    }
}
