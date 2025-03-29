package com.viewton;

import com.viewton.dto.Order;
import com.viewton.dto.ViewtonQuery;
import com.viewton.operator.EqualOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ViewtonQueryMapperTest {

    @Test
    @DisplayName("All params")
    void parseRequestParams_correct() {

        // given
        Map<String, String> requestParams = new HashMap<>() {{
            put("id", "1");
            put("name", "SomeName");
            put("sorting", "-id,name");
            put("page", "1");
            put("page_size", "10");
            put("attributes", "id,name");
            put("avg", "id,randomNumber[id,anotherId]");
            put("sum", "number,randomNumber[anotherId]");
            put("distinct", "true");
            put("count", "true");
        }};

        // when
        ViewtonQuery viewtonQuery = ViewtonQueryMapper.of(requestParams, 100);

        // then
        assertTrue(viewtonQuery.isDistinct());
        assertTrue(viewtonQuery.isCount());
        assertTrue(viewtonQuery.isSum());
        assertEquals(10, viewtonQuery.getPageSize());
        assertEquals(0, viewtonQuery.getPage());
        assertEquals(2, viewtonQuery.getRawWhereClauses().size());
        assertEquals(2, viewtonQuery.getAttributes().size());
        assertEquals(2, viewtonQuery.getRawOrderByes().size());

        // check name field order equals DESCENDING
        assertTrue(viewtonQuery.getRawOrderByes()
                .stream()
                .filter(orderBy -> "id".equals(orderBy.getFieldName()))
                .anyMatch(orderBy -> orderBy.getOrder() == Order.DESCENDING));

        // check name field order equals ASCENDING
        assertTrue(viewtonQuery.getRawOrderByes()
                .stream()
                .filter(orderBy -> "name".equals(orderBy.getFieldName()))
                .anyMatch(orderBy -> orderBy.getOrder() == Order.ASCENDING));
        assertEquals(EqualOperator.class, viewtonQuery.getRawWhereClauses().get(0).getOperator().getClass());
        assertEquals(EqualOperator.class, viewtonQuery.getRawWhereClauses().get(1).getOperator().getClass());

        // check contains id field in where clauses
        assertTrue(viewtonQuery.getRawWhereClauses()
                .stream()
                .anyMatch(clause -> "id".equals(clause.getFieldName())));

        // check contains name field in where clauses
        assertTrue(viewtonQuery.getRawWhereClauses()
                .stream()
                .anyMatch(clause -> "name".equals(clause.getFieldName())));

        // check id value is 1
        assertTrue(viewtonQuery.getRawWhereClauses()
                .stream()
                .filter(clause -> "id".equals(clause.getFieldName()))
                .findAny()
                .map(RawWhereClause::getValues)
                .stream()
                .anyMatch(values -> values.get(0).getValue().equals("1")));

        // check name value is SomeName
        assertTrue(viewtonQuery.getRawWhereClauses()
                .stream()
                .filter(clause -> "name".equals(clause.getFieldName()))
                .findAny()
                .map(RawWhereClause::getValues)
                .stream()
                .anyMatch(values -> values.get(0).getValue().equals("SomeName")));

        // check AVG is parsed and group by is correct
        assertNotNull(viewtonQuery.getAvg());
        assertTrue(viewtonQuery.getAvg().getGroupByAttributes().contains("id"));
        assertTrue(viewtonQuery.getAvg().getGroupByAttributes().contains("anotherId"));
        assertTrue(viewtonQuery.getAvg().getAttributes().contains("randomNumber"));
        assertTrue(viewtonQuery.getAvg().getAttributes().contains("id"));

        // check AVG is parsed and group by is correct
        assertNotNull(viewtonQuery.getSum());
        assertTrue(viewtonQuery.getSum().getGroupByAttributes().contains("anotherId"));
        assertTrue(viewtonQuery.getSum().getAttributes().contains("randomNumber"));
        assertTrue(viewtonQuery.getSum().getAttributes().contains("number"));
    }
}
