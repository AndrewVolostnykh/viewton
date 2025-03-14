package com.viewton;

import com.viewton.operator.GreaterOperator;
import com.viewton.operator.GreaterOrEqualOperator;
import com.viewton.operator.LessOperator;
import com.viewton.operator.LessOrEqualsOperator;
import com.viewton.operator.NotEqualOperator;
import com.viewton.operator.OrOperator;
import com.viewton.operator.RangeOperator;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A builder class for constructing request parameters for queries.
 * <p>
 * This class facilitates the construction of query parameters for filtering, pagination, sorting,
 * and other advanced options in a request URL which would be used for IPC.
 * It provides methods for defining various conditions
 * and attributes that will be included as URL parameters.
 * </p>
 * <p>
 * Extend it in final entity and specify exact params to allow this builder access fields.
 *
 * <p>Example of usage:</p>
 * <pre>
 * {@code
 *  class SomeEntity extends ViewtonQueryBuilder {
 *      private Long someField;
 *
 *
 *      static class SomeEntityViewtonQueryBuilder extends ViewtonQueryBuilder {
 *          public FilterBuilder<TestQueryBuilder> someField() {
 *                 return param("someField");
 *             }
 *      }
 *  }
 *  }
 *  </pre>
 * It allows you to build query for IPC:
 * <pre>
 *  {@code
 *
 *  }
 *  </pre>
 */
public class ViewtonQueryBuilder {

    private static final String PAGE_SIZE_PARAM_NAME = "page_size";
    private static final String SORTING_PARAM_NAME = "sorting";

    private final Map<String, String> params;

    public ViewtonQueryBuilder() {
        this.params = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    protected <B extends ViewtonQueryBuilder> FilterBuilder<B> param(String fieldName) {
        return new FilterBuilder<>(fieldName, (B) this);
    }

    protected <B extends ViewtonQueryBuilder> void registerParam(FilterBuilder<B> filterBuilder) {
        if (SORTING_PARAM_NAME.equals(filterBuilder.value())) {
            if (params.get(SORTING_PARAM_NAME) == null) {
                params.put(SORTING_PARAM_NAME, filterBuilder.key());
            } else {
                params.put(SORTING_PARAM_NAME, params.get(SORTING_PARAM_NAME) + "," + filterBuilder.key());
            }
        }

        this.params.put(filterBuilder.key(), filterBuilder.value());
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B page(int page) {
        params.put("page", String.valueOf(page));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B noPagination() {
        params.put(PAGE_SIZE_PARAM_NAME, "-1");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B pageSize(int pageSize) {
        params.put(PAGE_SIZE_PARAM_NAME, String.valueOf(pageSize));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B count() {
        params.put("count", "true");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B distinct() {
        params.put("distinct", "true");
        return (B) this;
    }

    public <B extends ViewtonQueryBuilder> B totalAttributes(Function<B, List<FilterBuilder<?>>> attributes) {
        return paramWithFields(attributes, "totalAttributes");
    }

    public <B extends ViewtonQueryBuilder> B attributes(Function<B, List<FilterBuilder<?>>> attributes) {
        return paramWithFields(attributes, "attributes");
    }

    @SuppressWarnings("unchecked")
    private <B extends ViewtonQueryBuilder> B paramWithFields(Function<B, List<FilterBuilder<?>>> attributes, String paramName) {
        Assert.notNull(attributes, "Attributes should not be null");
        List<String> attributesNames = attributes.apply((B) this).stream().map(FilterBuilder::key).collect(Collectors.toList());
        if (!attributesNames.isEmpty()) {
            params.put(paramName, String.join(",", attributesNames));
        }

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B attributes(FilterBuilder<?>... attributes) {
        if (attributes.length > 0) {
            List<String> attributesNames = Arrays.stream(attributes).map(FilterBuilder::key).collect(Collectors.toList());
            params.put("attributes", String.join(",", attributesNames));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonQueryBuilder> B totalAttributes(FilterBuilder<?>... totalAttributes) {
        if (totalAttributes.length > 0) {
            List<String> attributesNames = Arrays.stream(totalAttributes).map(FilterBuilder::key).collect(Collectors.toList());
            params.put("totalAttributes", String.join(",", attributesNames));
        }
        return (B) this;
    }

    public Map<String, String> build() {
        return params;
    }

    /**
     * A builder class for individual filters within a query.
     * <p>
     * This class allows for defining conditions such as "equal to", "greater than", "less than",
     * and sorting. These filters are then registered with the parent {@link ViewtonQueryBuilder}.
     * </p>
     *
     * @param <B> the type of the parent builder
     */
    public static class FilterBuilder<B extends ViewtonQueryBuilder> {

        protected String fieldName;
        protected String value;
        protected B caller;
        protected boolean ignoreCase;

        public FilterBuilder(Object fieldName, B caller) {
            this.fieldName = fieldName.toString();
            this.caller = caller;
        }

        public FilterBuilder<B> ignoreCase() {
            this.ignoreCase = true;
            return this;
        }

        public B lessThenOrEqual(Object value) {
            this.value = new LessOrEqualsOperator().getValue() + value.toString();
            caller.registerParam(this);
            return caller;
        }

        public B greaterThanOrEqual(Object value) {
            this.value = new GreaterOrEqualOperator().getValue() + value.toString();
            caller.registerParam(this);
            return caller;
        }

        public B equalsTo(Object to) {
            this.value = to.toString();
            if (ignoreCase) {
                this.value = "^" + this.value;
                ignoreCase = false;
            }
            caller.registerParam(this);
            return caller;
        }

        public B notEqualsTo(Object to) {
            this.value = new NotEqualOperator().getValue() + to.toString();
            if (ignoreCase) {
                this.value = "^" + this.value;
                ignoreCase = false;
            }
            caller.registerParam(this);
            return caller;
        }

        public B between(Object lower, Object greater) {
            this.value = lower.toString() + new RangeOperator().getValue() + greater.toString();
            caller.registerParam(this);
            return caller;
        }

        public B greater(Object than) {
            this.value = new GreaterOperator().getValue() + than.toString();
            caller.registerParam(this);
            return caller;
        }

        public B less(Object than) {
            this.value = new LessOperator().getValue() + than.toString();
            caller.registerParam(this);
            return caller;
        }

        public OrAndBuilder<B> or(Object value) {
            this.value = value.toString();
            if (ignoreCase) {
                this.value = "^" + this.value;
                ignoreCase = false;
            }
            return new OrAndBuilder<>(caller, this);
        }

        public B ascSort() {
            this.value = SORTING_PARAM_NAME;
            caller.registerParam(this);
            return caller;
        }

        public B descSort() {
            this.value = SORTING_PARAM_NAME;
            this.fieldName = "-" + fieldName;
            caller.registerParam(this);
            return caller;
        }

        String key() {
            return fieldName;
        }

        String value() {
            return value;
        }

        void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * A helper class for building OR conditions for filters.
     *
     * @param <B> the type of the parent builder
     */
    public static class OrAndBuilder<B extends ViewtonQueryBuilder> {
        private final B caller;
        private final FilterBuilder<B> param;
        boolean ignoreCase;

        public OrAndBuilder(B caller, FilterBuilder<B> param) {
            this.caller = caller;
            this.param = param;
        }

        public B next() {
            caller.registerParam(param);
            return caller;
        }

        public OrAndBuilder<B> ignoreCase() {
            this.ignoreCase = true;
            return this;
        }

        public OrAndBuilder<B> or(String value) {
            if (ignoreCase) {
                value = "^" + value;
                ignoreCase = false;
            }
            param.setValue(param.value() + new OrOperator().getValue() + value);
            return this;
        }
    }
}

