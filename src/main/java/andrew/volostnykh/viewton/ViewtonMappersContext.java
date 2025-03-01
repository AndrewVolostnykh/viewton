package andrew.volostnykh.viewton;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Context class that holds mappers for extracting query parameters from the URL and converting them
 * into appropriate objects used in building queries. This class provides a flexible way to map various
 * query parameters (such as filters, sorting, paging) to the corresponding Java objects needed for
 * constructing the SQL query via JPA Criteria API.
 *
 * <p>The mappers are defined as static fields and can be customized by registering new mappers.</p>
 *
 * <p>Example of how mappers can be used:</p>
 * <pre>
 * Map<String, String> queryParams = ...;
 * List<RawWhereClause> whereClauses = ViewtonMappersContext.mapWhereClauses.apply(queryParams);
 * List<RawOrderBy> orderBy = ViewtonMappersContext.mapOrderByes.apply(queryParams);
 * </pre>
 */
public class ViewtonMappersContext {

    static Function<Map<String, String>, List<? extends RawWhereClause>> mapWhereClauses;
    static Function<Map<String, String>, List<RawOrderBy>> mapOrderByes;
    static Function<Map<String, String>, List<String>> mapAttributes;
    static Function<Map<String, String>, List<String>> mapTotalAttributes;
    static Function<Map<String, String>, Integer> mapPage;
    static BiFunction<Map<String, String>, Integer, Integer> mapPageSize;
    static Function<Map<String, String>, Boolean> isDistinct;
    static Function<Map<String, String>, Boolean> isCount;
    static Function<Map<String, String>, Boolean> isTotal;

    static {
        mapWhereClauses = DefaultQueryMapperMethods::mapRawWhereClauses;
        mapOrderByes = DefaultQueryMapperMethods::mapOrderByes;
        mapPage = DefaultQueryMapperMethods::mapPage;
        mapPageSize = DefaultQueryMapperMethods::mapPageSize;
        mapAttributes = DefaultQueryMapperMethods::mapAttributes;
        mapTotalAttributes = DefaultQueryMapperMethods::mapTotalAttributes;
        isDistinct = DefaultQueryMapperMethods::isDistinct;
        isCount = DefaultQueryMapperMethods::isCount;
        isTotal = DefaultQueryMapperMethods::isTotal;
    }

    public static void assignPageSizeMapper(BiFunction<Map<String, String>, Integer, Integer> mapper) {
        mapPageSize = mapper;
    }

    public static void assignIsTotalMapper(Function<Map<String, String>, Boolean> mapper) {
        isTotal = mapper;
    }

    public static void assignIsCountMapper(Function<Map<String, String>, Boolean> mapper) {
        isCount = mapper;
    }

    public static void assignIsDistinctMapper(Function<Map<String, String>, Boolean> mapper) {
        isDistinct = mapper;
    }

    public static void assignPageMapper(Function<Map<String, String>, Integer> mapper) {
        mapPage = mapper;
    }

    public static void assignTotalAttributesMapper(Function<Map<String, String>, List<String>> mapper) {
        mapTotalAttributes = mapper;
    }

    public static void assignAttributesMapper(Function<Map<String, String>, List<String>> mapper) {
        mapAttributes = mapper;
    }

    public static void assignWhereClausesMapper(Function<Map<String, String>, List<? extends RawWhereClause>> mapper) {
        mapWhereClauses = mapper;
    }

    public static void assignOrderByMapper
            (Function<Map<String, String>, List<RawOrderBy>> mapper) {
        mapOrderByes = mapper;
    }
}
