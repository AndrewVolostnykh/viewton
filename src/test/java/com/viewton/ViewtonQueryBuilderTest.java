package com.viewton;

import com.viewton.operator.GreaterOperator;
import com.viewton.operator.GreaterOrEqualOperator;
import com.viewton.operator.LessOperator;
import com.viewton.operator.LessOrEqualsOperator;
import com.viewton.operator.NotEqualOperator;
import com.viewton.operator.RangeOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.viewton.ViewtonQueryBuilderTest.TestView.TestQueryBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewtonQueryBuilderTest {

    private static final String lowerDate = "12-12-1999";
    private static final String greaterDate = "12-12-2022";
    private static final String notEqualsId = "123";
    private static final String equalsName = "TESTNAME";
    private static final String greaterThanOrEqual = "111";
    private static final String lessThanOrEqual = "222";
    private static final String lessThan = "333";
    private static final String greaterThan = "444";
    private static final String orValue = "555";

    @Test
    @DisplayName("Single parameter")
    void testBuildValidMapWithOneFilter() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder.date()
                .between(lowerDate, greaterDate)
                .build();

        assertEquals(lowerDate + new RangeOperator().getValue() + greaterDate, params.get("date"));
    }

    @Test
    @DisplayName("Operators range, between, equality, or, great/less")
    void testBuildFiltersWithValidOperators() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder
                .date().between(lowerDate, greaterDate)
                .id().notEqualsTo(notEqualsId)
                .name().equalsTo(equalsName)
                .test1().greaterThanOrEqual(greaterThanOrEqual)
                .test2().lessThenOrEqual(lessThanOrEqual)
                .test3().less(lessThan)
                .test4().greater(greaterThan)
                .test5().or(orValue).next()
                .build();

        assertEquals(lowerDate + new RangeOperator().getValue() + greaterDate, params.get("date"));
        assertEquals(new NotEqualOperator().getValue() + notEqualsId, params.get("id"));
        assertEquals(equalsName, params.get("name"));
        assertEquals(new GreaterOrEqualOperator().getValue() + greaterThanOrEqual, params.get("test1"));
        assertEquals(new LessOrEqualsOperator().getValue() + lessThanOrEqual, params.get("test2"));
        assertEquals(new LessOperator().getValue() + lessThan, params.get("test3"));
        assertEquals(new GreaterOperator().getValue() + greaterThan, params.get("test4"));
        assertEquals(orValue, params.get("test5"));
    }

    @Test
    @DisplayName("Several 'OR' values")
    void testBuilderOrSeveralValues() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder
                .id().or("1").or("2").or("444").next()
                .build();

        assertEquals("1|2|444", params.get("id"));
    }

    @Test
    @DisplayName("Distinct, pagination, total, attributes selection")
    void testBuildValidTableViewParams() {
        TestQueryBuilder builder = new TestQueryBuilder();

        int page = 1;
        int pageSize = 10;

        Map<String, String> params = builder.count()
                .distinct()
                .page(page)
                .pageSize(pageSize)
                .attributes(builder.id(), builder.name(), builder.test5())
                .totalAttributes(builder.test1(), builder.test2())
                .build();

        assertEquals("true", params.get("distinct"));
        assertEquals(String.valueOf(page), params.get("page"));
        assertEquals(String.valueOf(pageSize), params.get("page_size"));
        assertEquals("true", params.get("total"));
        assertEquals("id,name,test5", params.get("attributes"));
        assertEquals("test1,test2", params.get("totalAttributes"));
    }

    @Test
    @DisplayName("Inner attributes selector")
    void testBuildAttributesWithFunctionBasedMethod() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder.count()
                .attributes((TestQueryBuilder thisBuilder) -> List.of(thisBuilder.id(), thisBuilder.name(), thisBuilder.test5()))
                .totalAttributes((TestQueryBuilder thisBuilder) -> List.of(builder.test1(), builder.test2()))
                .build();

        assertEquals("true", params.get("total"));
        assertEquals("id,name,test5", params.get("attributes"));
        assertEquals("test1,test2", params.get("totalAttributes"));
    }

    @Test
    @DisplayName("Several sorting params")
    void testSortingParams() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder.date().ascSort()
                .test2().ascSort()
                .test3().descSort()
                .test4().descSort()
                .build();

        assertEquals("date,test2,-test3,-test4", params.get("sorting"));
    }

    @Test
    @DisplayName("Several 'OR' operators")
    void severalOR() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder.test1()
                .or("firstValue")
                .or("secondValue")
                .or("thirdValue")
                .next().build();

        assertEquals("firstValue|secondValue|thirdValue", result.get("test1"));
    }

    @Test
    @DisplayName("Ignore case equalsTo")
    void equalsToIgnoreCase() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder.test1()
                .ignoreCase()
                .equalsTo("someValue")
                .build();

        assertEquals("^someValue", result.get("test1"));
    }

    @Test
    @DisplayName("Ignore case equalsTo two params")
    void equalsIgnoreCaseForOnlyOneOfTwoParams() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder
                .test5().equalsTo("someAnotherValue")
                .test1().ignoreCase().equalsTo("someValue")
                .build();

        assertEquals("^someValue", result.get("test1"));
        assertEquals("someAnotherValue", result.get("test5"));
    }

    @Test
    @DisplayName("Ignore case notEqualsTo")
    void ignoreCaseNotEqualsTo() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder
                .test5().ignoreCase().notEqualsTo("someValue")
                .build();

        assertEquals("^<>someValue", result.get("test5"));
    }

    @Test
    @DisplayName("Ignore case 'OR'")
    void ignoreCaseOr() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder
                .test5().ignoreCase().or("someValue").or("another")
                .next()
                .build();

        assertEquals("^someValue|another", result.get("test5"));
    }

    @Test
    @DisplayName("Ignore case several 'OR'")
    void ignoreCaseSeveralOr() {
        TestQueryBuilder builder = new TestQueryBuilder();
        Map<String, String> result = builder
                .test5().ignoreCase().or("someValue").ignoreCase().or("another")
                .next()
                .build();

        assertEquals("^someValue|^another", result.get("test5"));
    }

    @Test
    @DisplayName("No pagination set 'page_size' param to -1")
    void testNoPagination() {
        TestQueryBuilder builder = new TestQueryBuilder();

        Map<String, String> params = builder.noPagination().build();

        assertEquals("-1", params.get("page_size"));
    }

    static class TestView {

        private Long id;
        private String name;
        private LocalDate date;
        private String test1;
        private String test2;
        private String test3;
        private String test4;
        private String test5;

        static class TestQueryBuilder extends ViewtonQueryBuilder {

            public FilterBuilder<TestQueryBuilder> id() {
                return param("id");
            }

            public FilterBuilder<TestQueryBuilder> name() {
                return param("name");
            }

            public FilterBuilder<TestQueryBuilder> date() {
                return param("date");
            }

            public FilterBuilder<TestQueryBuilder> test1() {
                return param("test1");
            }

            public FilterBuilder<TestQueryBuilder> test2() {
                return param("test2");
            }

            public FilterBuilder<TestQueryBuilder> test3() {
                return param("test3");
            }

            public FilterBuilder<TestQueryBuilder> test4() {
                return param("test4");
            }

            public FilterBuilder<TestQueryBuilder> test5() {
                return param("test5");
            }
        }
    }

}
