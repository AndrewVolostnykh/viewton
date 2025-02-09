package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.dto.ViewtonResponseDto;
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
import org.hibernate.jpa.spi.NativeQueryTupleTransformer;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
@Transactional(readOnly = true)
public class ViewtonRepository {

    private final int defaultPageSize;
    private final EntityManager entityManager;

    @Autowired
    public ViewtonRepository(
            @Value("${viewton.request.default-page-size:-1}") int defaultPageSize,
            EntityManager entityManager) {
        this.defaultPageSize = defaultPageSize;
        this.entityManager = entityManager;
    }

    public <T> ViewtonResponseDto<T> list(Map<String, String> requestParams, Class<T> entityType) {
        ViewtonQuery viewtonQuery = ViewtonQueryMapper.of(requestParams, defaultPageSize);

        return new ViewtonResponseDto<>(
                list(viewtonQuery, entityType),
                total(viewtonQuery, entityType),
                count(viewtonQuery, entityType)
        );
    }

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

    public <T> T total(ViewtonQuery query, Class<T> entityType) {
        if (query.doNotTotals()) {
            return null;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> basicQuery = cb.createTupleQuery();
        Root<T> root = basicQuery.from(entityType);

        CriteriaQuery<Tuple> criteriaQuery = basicQuery
                .multiselect(getTotalColumns(query.getTotalAttributes(), cb, root))
                .where(WherePredicatesConverter.convert(query.getRawWhereClauses(), root, cb)
                        .toArray(new Predicate[0]));

        return ((Session) entityManager.getDelegate())
                .createQuery(criteriaQuery)
                .uniqueResultOptional()
                .map(Tuple::toArray)
                .map(tuples -> new AliasToBeanResultTransformer(entityType).transformTuple(
                        tuples, query.getTotalAttributes().toArray(new String[0]))
                )
                .map(entityType::cast)
                .get();
    }

    private List<Order> getOrders(List<RawOrderBy> orders, Root root, CriteriaBuilder cb) {
        return orders
                .stream()
                .map(orderBy -> {
                    Path path = root.get(orderBy.getFieldName());
                    return orderBy.isAscending() ? cb.asc(path) : cb.desc(path);
                })
                .toList();
    }

    private <T> List<String> getAttributes(ViewtonQuery query, Root<T> root) {
        return query.getAttributes() == null
                ? getDefaultAttributes(root)
                : query.getAttributes();
    }

    private <T> Expression[] getSelections(List<String> attributes, Root<T> root) {
        return attributes.stream()
                .map(root::get)
                .toArray(Expression[]::new);
    }

    private <T> List<String> getDefaultAttributes(Root<T> root) {
        return root.getModel().getAttributes().stream()
                .map(Attribute::getName)
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private <T> Expression<Number>[] getTotalColumns(List<String> totalFields, CriteriaBuilder cb, Root<T> root) {
        return totalFields.stream()
                .map(totalField -> cb.sum(root.get(totalField)))
                .toArray(Expression[]::new);
    }
}
