package com.viewton;

import com.viewton.dto.AggregateAttributes;
import com.viewton.dto.AvgAttributes;
import com.viewton.dto.RawOrderBy;
import com.viewton.dto.SumAttributes;
import com.viewton.dto.ViewtonQuery;
import com.viewton.dto.ViewtonResponseDto;
import com.viewton.utils.ViewtonReflections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Repository for performing dynamic queries using JPA Criteria API.
 * This repository builds and executes queries based on `ViewtonQuery` and maps the results
 * to entities with optional pagination, filtering, sorting, and aggregation.
 * <p>
 * It supports querying for entities with dynamic where clauses, custom ordering,
 * attribute selection, and provides options for counting and aggregating data.
 *
 * <p>Methods in this repository rely on the `ViewtonQuery` object to encapsulate user-provided
 * parameters such as filters (where clauses), sort order, page size, and selected attributes.
 * The queries are constructed using JPA's Criteria API, and the results are returned as DTOs
 * or as paginated lists of entities.</p>
 *
 * <p>The repository also supports transactions and is read-only to prevent modification of
 * entities during query execution.</p>
 *
 * <p>This repository is meant to be used in the context of a Spring-based application,
 * with a focus on flexible querying of JPA entities based on user-supplied parameters.</p>
 */
@Repository
@Transactional(readOnly = true)
public class ViewtonRepository {

    private final int defaultPageSize;
    private final EntityManager entityManager;

    /**
     * Constructs a new `ViewtonRepository` with the specified default page size and `EntityManager`.
     *
     * @param defaultPageSize The default page size to be used when pagination is not specified.
     * @param entityManager   The JPA `EntityManager` used to execute queries.
     */
    @Autowired
    public ViewtonRepository(
            @Value("${viewton.request.default-page-size:-1}") int defaultPageSize,
            EntityManager entityManager) {
        this.defaultPageSize = defaultPageSize;
        this.entityManager = entityManager;
    }

    /**
     * Returns a paginated response of entities based on the provided request parameters.
     * The method builds a `ViewtonQuery` from the request parameters and executes the query
     * to retrieve the results, along with count and totals.
     *
     * @param requestParams A map of request parameters used to build the `ViewtonQuery`.
     * @param entityType    The entity class type to query.
     * @param <T>           The entity type.
     * @return A `ViewtonResponseDto` containing the results of the query, count, and totals.
     */
    public <T> ViewtonResponseDto<T> list(Map<String, String> requestParams, Class<T> entityType) {
        ViewtonQuery viewtonQuery = ViewtonQueryMapper.of(requestParams, defaultPageSize);

        return new ViewtonResponseDto<>(
                list(viewtonQuery, entityType),
                sum(viewtonQuery, entityType),
                avg(viewtonQuery, entityType),
                count(viewtonQuery, entityType)
        );
    }

    /**
     * Executes the query using the provided `ViewtonQuery` and returns a paginated list of entities.
     *
     * @param query      The `ViewtonQuery` containing the filtering, sorting, and pagination parameters.
     * @param entityType The entity class type to query.
     * @param <T>        The entity type.
     * @return A list of entities matching the query criteria.
     */
    public <T> List<T> list(ViewtonQuery query, Class<T> entityType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<T> root = criteriaQuery.from(entityType);

        List<String> mappingAttributes = getAttributes(query, root);
        Query<Tuple> resultQuery = Optional.of(criteriaQuery)
                .map(q -> q.where(WherePredicatesConverter.convert(query.getRawWhereClauses(), root, cb)
                        .toArray(new Predicate[0])))
                .map(q -> q.orderBy(getOrders(query.getRawOrderByes(), root, cb)))
                .map(q -> q.multiselect(getSelections(mappingAttributes, root)).distinct(query.isDistinct()))
                .map(q -> ((Session) entityManager.getDelegate()).createQuery(q))
                .map(q -> q.setFirstResult(query.getPage()).setMaxResults(query.getPageSize()))
                .orElseThrow(() -> new IllegalStateException("Unable to construct final query"));

        return resultQuery
                .stream()
                .map(Tuple::toArray)
                .map(tuples -> new AliasToBeanResultTransformer(entityType)
                        .transformTuple(tuples, mappingAttributes.toArray(new String[0])))
                .map(entityType::cast)
                .collect(toList());
    }

    /**
     * Returns the count of results that match the given `ViewtonQuery`.
     *
     * @param query       The `ViewtonQuery` containing the filtering and pagination parameters.
     * @param entityClass The entity class to query.
     * @param <T>         The entity type.
     * @return The count of entities matching the query.
     */
    public <T> long count(ViewtonQuery query, Class<T> entityClass) {
        if (query.doNotCount()) {
            return 0;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> basicQuery = cb.createQuery(Long.class);
        Root<T> root = basicQuery.from(entityClass);

        Expression<Long> countSelection = query.isDistinct() ? cb.countDistinct(root) : cb.count(root);
        CriteriaQuery<Long> criteriaQuery = basicQuery
                .select(countSelection)
                .where(WherePredicatesConverter.convert(query.getRawWhereClauses(), root, cb)
                        .toArray(new Predicate[0]));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Returns the total values for specified attributes in the given `ViewtonQuery`.
     *
     * @param query      The `ViewtonQuery` containing the total attributes and filtering parameters.
     * @param entityType The entity class type to query.
     * @param <T>        The entity type.
     * @return The total values for the specified attributes.
     */
    public <T> List<T> sum(ViewtonQuery query, Class<T> entityType) {
        if (query.doNotSum()) {
            return null;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> basicQuery = cb.createTupleQuery();
        Root<T> root = basicQuery.from(entityType);

        CriteriaQuery<Tuple> criteriaQuery = basicQuery
                .multiselect(getSumColumns(query.getSum(), cb, root))
                .groupBy(getGroupByColumns(query.getSum().getGroupByAttributes(), root))
                .where(WherePredicatesConverter.convert(query.getRawWhereClauses(), root, cb)
                        .toArray(new Predicate[0]));

        return ((Session) entityManager.getDelegate())
                .createQuery(criteriaQuery)
                .setFirstResult(query.getPage()).setMaxResults(query.getPageSize())
                .stream()
                .map(Tuple::toArray)
                .map(tuples -> new AliasToBeanResultTransformer(entityType).transformTuple(
                        tuples, query.getSum().getAllFields(ViewtonReflections::getSumAliases, entityType).toArray(new String[0]))
                )
                .map(entityType::cast)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the average values for the specified attributes in the given query.
     *
     * <p>This method performs a query to calculate the average values of the attributes
     * specified in the query, grouped by the appropriate columns, and returns the result
     * as an entity of the provided type. If averaging is not required (as determined by
     * the `doNotAvg` flag in the query), the method returns {@code null}.</p>
     *
     * @param query      the {@link ViewtonQuery} containing the needed attributes for the average calculation
     *                   and the attributes to be averaged
     * @param entityType the class type of the entity to be returned as the result of the query
     * @param <T>        the type of the entity to be returned
     * @return the calculated average values, {@code null} if avg not needed.
     */
    public <T> List<T> avg(ViewtonQuery query, Class<T> entityType) {
        if (query.doNotAvg()) {
            return null;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> basicQuery = cb.createTupleQuery();
        Root<T> root = basicQuery.from(entityType);

        CriteriaQuery<Tuple> criteriaQuery = basicQuery
                .multiselect(getAvgColumns(query.getAvg(), cb, root))
                .groupBy(getGroupByColumns(query.getAvg().getGroupByAttributes(), root))
                .where(WherePredicatesConverter.convert(query.getRawWhereClauses(), root, cb)
                        .toArray(new Predicate[0]));

        return ((Session) entityManager.getDelegate())
                .createQuery(criteriaQuery)
                .setFirstResult(query.getPage()).setMaxResults(query.getPageSize())
                .stream()
                .map(Tuple::toArray)
                .map(tuples -> new AliasToBeanResultTransformer(entityType).transformTuple(
                        tuples, query.getAvg().getAllFields(ViewtonReflections::getAvgAliases, entityType).toArray(new String[0]))
                )
                .map(entityType::cast)
                .collect(Collectors.toList());
    }

    /**
     * Builds a list of `Order` clauses based on the given order by criteria.
     *
     * @param orders The list of `RawOrderBy` objects representing the ordering criteria.
     * @param root   The root entity path used for ordering.
     * @param cb     The CriteriaBuilder used to create the order expressions.
     * @return A list of `Order` objects.
     */
    private List<Order> getOrders(List<RawOrderBy> orders, Root root, CriteriaBuilder cb) {
        return orders
                .stream()
                .map(orderBy -> {
                    Path path = root.get(orderBy.getFieldName());
                    return orderBy.isAscending() ? cb.asc(path) : cb.desc(path);
                })
                .toList();
    }

    /**
     * Retrieves the attributes to be selected in the query.
     * If no attributes are provided in the query, defaults to all attributes of the entity.
     *
     * @param query The `ViewtonQuery` containing the requested attributes.
     * @param root  The root entity path used for selecting the attributes.
     * @param <T>   The entity type.
     * @return A list of attribute names to be selected.
     */
    private <T> List<String> getAttributes(ViewtonQuery query, Root<T> root) {
        return query.getAttributes() == null
                ? getDefaultAttributes(root)
                : query.getAttributes();
    }

    /**
     * Returns a list of `Expression` objects representing the selected attributes for the query.
     *
     * @param attributes The list of attribute names to be selected.
     * @param root       The root entity path used for selecting the attributes.
     * @param <T>        The entity type.
     * @return An array of `Expression` objects for selecting the attributes.
     */
    private <T> Expression[] getSelections(List<String> attributes, Root<T> root) {
        return attributes.stream()
                .map(root::get)
                .toArray(Expression[]::new);
    }

    /**
     * Returns the default attributes for the entity if no specific attributes are requested.
     *
     * @param root The root entity path.
     * @param <T>  The entity type.
     * @return A list of default attribute names.
     */
    private <T> List<String> getDefaultAttributes(Root<T> root) {
        return root.getModel().getAttributes().stream()
                .map(Attribute::getName)
                .collect(toList());
    }

    /**
     * Retrieves the sum columns (sum expressions) for the specified fields in the query.
     *
     * @param sumAttributes The list of total fields to be summed.
     * @param cb        The CriteriaBuilder used to build sum expressions.
     * @param root      The root entity path.
     * @param <T>       The entity type.
     * @return An array of `Expression<Number>` representing the sum of the total fields.
     */
    @SuppressWarnings("unchecked")
    private <T> Expression<Number>[] getSumColumns(SumAttributes sumAttributes, CriteriaBuilder cb, Root<T> root) {
        return mapAggregateColumns(sumAttributes, cb::sum, root);
    }

    private <T, N extends Number> Expression[] mapAggregateColumns(AggregateAttributes aggregateAttributes, Function<Expression<N>, Expression<N>> expressionQuery, Root<T> root) {
        Expression[] groupByExpressions;
        if (aggregateAttributes.getGroupByAttributes() == null) {
            groupByExpressions = new Expression[0];
        } else {
            groupByExpressions = aggregateAttributes.getGroupByAttributes().stream()
                    .map(groupBy -> root.get(groupBy))
                    .toArray(Expression[]::new);
        }

        Expression[] sumExpression = aggregateAttributes.getAttributes().stream()
                .map(avgField -> expressionQuery.apply(root.get(avgField)))
                .toArray(Expression[]::new);

        return Stream.concat(Arrays.stream(groupByExpressions), Arrays.stream(sumExpression))
                .toArray(Expression[]::new);
    }

    @SuppressWarnings("unchecked")
    private <T> Expression[] getAvgColumns(AvgAttributes avgAttributes, CriteriaBuilder cb, Root<T> root) {
        return mapAggregateColumns(avgAttributes, cb::sum, root);
    }

    private <T> Expression[] getGroupByColumns(List<String> groupByFields, Root<T> root) {
        if (groupByFields == null) {
            return new Expression[0];
        }

        return groupByFields.stream()
                .map(groupBy -> root.get(groupBy))
                .toArray(Expression[]::new);
    }
}