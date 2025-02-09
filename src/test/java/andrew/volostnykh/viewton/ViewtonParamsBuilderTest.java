package andrew.volostnykh.viewton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static andrew.volostnykh.viewton.ViewtonParamsBuilderTest.TestView.TestParamsBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewtonParamsBuilderTest {

    private static final String lowerDate = "12-12-1999";
    private static final String greaterDate = "12-12-2022";
    private static final String notEqualsId = "123";
    private static final String equalsName = "TESTNAME";
    private static final String greaterThanOrEqual = "111";
    private static final String lessThanOrEqual = "222";
    private static final String lessThan = "333";
    private static final String greaterThan = "444";
    private static final String orValue = "555";

    static class TestView {

        private Long id;
        private String name;
        private LocalDate date;
        private String test1;
        private String test2;
        private String test3;
        private String test4;
        private String test5;

        static class TestParamsBuilder extends ViewtonParamsBuilder {

            public FilterBuilder<TestParamsBuilder> id() {
                return param("id");
            }

            public FilterBuilder<TestParamsBuilder> name() {
                return param("name");
            }

            public FilterBuilder<TestParamsBuilder> date() {
                return param("date");
            }

            public FilterBuilder<TestParamsBuilder> test1() {
                return param("test1");
            }

            public FilterBuilder<TestParamsBuilder> test2() {
                return param("test2");
            }

            public FilterBuilder<TestParamsBuilder> test3() {
                return param("test3");
            }

            public FilterBuilder<TestParamsBuilder> test4() {
                return param("test4");
            }

            public FilterBuilder<TestParamsBuilder> test5() {
                return param("test5");
            }
        }
    }

    @Test
    @DisplayName("Single parameter built correctly")
    void testBuildValidMapWithOneFilter() {
        TestParamsBuilder builder = new TestParamsBuilder();

        Map<String, String> params = builder.date()
                .between(lowerDate, greaterDate)
                .build();

        assertEquals(lowerDate + Operator.RANGE.getValue() + greaterDate, params.get("date"));
    }

    @Test
    @DisplayName("Operators range, between, equality, or, great/less built correctly")
    void testBuildFiltersWithValidOperators() {
        TestParamsBuilder builder = new TestParamsBuilder();

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

        assertEquals(lowerDate + Operator.RANGE.getValue() + greaterDate, params.get("date"));
        assertEquals(Operator.NOT_EQUAL.getValue() + notEqualsId, params.get("id"));
        assertEquals(equalsName, params.get("name"));
        assertEquals(Operator.GREATER_OR_EQUAL.getValue() + greaterThanOrEqual, params.get("test1"));
        assertEquals(Operator.LESS_OR_EQUAL.getValue() + lessThanOrEqual, params.get("test2"));
        assertEquals(Operator.LESS.getValue() + lessThan, params.get("test3"));
        assertEquals(Operator.GREATER.getValue() + greaterThan, params.get("test4"));
        assertEquals(orValue, params.get("test5"));
    }

    @Test
    @DisplayName("Several 'OR' values built correct")
    void testBuilderOrSeveralValues() {
        TestParamsBuilder builder = new TestParamsBuilder();

        Map<String, String> params = builder
                .id().or("1").or("2").or("444").next()
                .build();

        assertEquals("1|2|444", params.get("id"));
    }

    @Test
    @DisplayName("Distinct, pagination, total, attributes selection built correctly")
    void testBuildValidTableViewParams() {
        TestParamsBuilder builder = new TestParamsBuilder();

        int page = 1;
        int pageSize = 10;

        Map<String, String> params = builder.count()
                .distinct()
                .page(page)
                .pageSize(pageSize)
                .total()
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
    @DisplayName("Inner attributes selector built correctly")
    void testBuildAttributesWithFunctionBasedMethod() {
        TestParamsBuilder builder = new TestParamsBuilder();

        Map<String, String> params = builder.count()
                .total()
                .attributes((TestParamsBuilder thisBuilder) -> List.of(thisBuilder.id(), thisBuilder.name(), thisBuilder.test5()))
                .totalAttributes((TestParamsBuilder thisBuilder) -> List.of(builder.test1(), builder.test2()))
                .build();

        assertEquals("true", params.get("total"));
        assertEquals("id,name,test5", params.get("attributes"));
        assertEquals("test1,test2", params.get("totalAttributes"));
    }

    @Test
    @DisplayName("Several sorting params built correctly")
    void testSortingParams() {
        TestParamsBuilder builder = new TestParamsBuilder();

        Map<String, String> params = builder.date().ascSort()
                .test2().ascSort()
                .test3().descSort()
                .test4().descSort()
                .build();

        assertEquals("date,test2,-test3,-test4", params.get("sorting"));
    }

    @Test
    @DisplayName("No pagination set 'page_size' param to -1")
    void testNoPagination() {
        TestParamsBuilder builder = new TestParamsBuilder();

        Map<String, String> params = builder.noPagination().build();

        assertEquals("-1", params.get("page_size"));
    }

}
