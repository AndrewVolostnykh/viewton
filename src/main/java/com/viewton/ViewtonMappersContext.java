package com.viewton;

import com.viewton.dto.AvgAttributes;
import com.viewton.dto.RawOrderBy;
import com.viewton.dto.SumAttributes;
import com.viewton.lang.NoneThreadSafe;

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
    static Function<Map<String, String>, SumAttributes> mapSumAttributes;
    static Function<Map<String, String>, AvgAttributes> mapAvgAttributes;
    static Function<Map<String, String>, Integer> mapPage;
    static BiFunction<Map<String, String>, Integer, Integer> mapPageSize;
    static Function<Map<String, String>, Boolean> isDistinct;
    static Function<Map<String, String>, Boolean> isCount;
    static Function<Map<String, String>, Boolean> isSum;

    static {
        mapWhereClauses = DefaultQueryMapperMethods::mapRawWhereClauses;
        mapOrderByes = DefaultQueryMapperMethods::mapOrderByes;
        mapPage = DefaultQueryMapperMethods::mapPage;
        mapPageSize = DefaultQueryMapperMethods::mapPageSize;
        mapAttributes = DefaultQueryMapperMethods::mapAttributes;
        mapSumAttributes = DefaultQueryMapperMethods::mapSumAttributes;
        mapAvgAttributes = DefaultQueryMapperMethods::mapAvgAttributes;
        isDistinct = DefaultQueryMapperMethods::isDistinct;
        isCount = DefaultQueryMapperMethods::isCount;
        isSum = DefaultQueryMapperMethods::isSum;
    }

    @NoneThreadSafe
    public static void assignPageSizeMapper(BiFunction<Map<String, String>, Integer, Integer> mapper) {
        mapPageSize = mapper;
    }

    @NoneThreadSafe
    public static void assignIsSumMapper(Function<Map<String, String>, Boolean> mapper) {
        isSum = mapper;
    }

    @NoneThreadSafe
    public static void assignIsCountMapper(Function<Map<String, String>, Boolean> mapper) {
        isCount = mapper;
    }

    @NoneThreadSafe
    public static void assignIsDistinctMapper(Function<Map<String, String>, Boolean> mapper) {
        isDistinct = mapper;
    }

    @NoneThreadSafe
    public static void assignPageMapper(Function<Map<String, String>, Integer> mapper) {
        mapPage = mapper;
    }

    @NoneThreadSafe
    public static void assignAvgAttributesMapper(Function<Map<String, String>, AvgAttributes> mapper) {
        mapAvgAttributes = mapper;
    }

    @NoneThreadSafe
    public static void assignTotalAttributesMapper(Function<Map<String, String>, SumAttributes> mapper) {
        mapSumAttributes = mapper;
    }

    @NoneThreadSafe
    public static void assignAttributesMapper(Function<Map<String, String>, List<String>> mapper) {
        mapAttributes = mapper;
    }

    @NoneThreadSafe
    public static void assignWhereClausesMapper(Function<Map<String, String>, List<? extends RawWhereClause>> mapper) {
        mapWhereClauses = mapper;
    }

    @NoneThreadSafe
    public static void assignOrderByMapper
            (Function<Map<String, String>, List<RawOrderBy>> mapper) {
        mapOrderByes = mapper;
    }
}
