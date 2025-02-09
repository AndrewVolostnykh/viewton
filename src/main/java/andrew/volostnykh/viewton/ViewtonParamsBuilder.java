package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.operator.GreaterOperator;
import andrew.volostnykh.viewton.operator.GreaterOrEqualOperator;
import andrew.volostnykh.viewton.operator.LessOperator;
import andrew.volostnykh.viewton.operator.LessOrEqualsOperator;
import andrew.volostnykh.viewton.operator.NotEqualOperator;
import andrew.volostnykh.viewton.operator.Operator;
import andrew.volostnykh.viewton.operator.OrOperator;
import andrew.volostnykh.viewton.operator.RangeOperator;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewtonParamsBuilder {

    private static final String PAGE_SIZE_PARAM_NAME = "page_size";
    private static final String SORTING_PARAM_NAME = "sorting";

    private final Map<String, String> params;

    public ViewtonParamsBuilder() {
        this.params = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    protected <B extends ViewtonParamsBuilder> FilterBuilder<B> param(String fieldName) {
        return new FilterBuilder<>(fieldName, (B) this);
    }

    protected <B extends ViewtonParamsBuilder> void registerParam(FilterBuilder<B> filterBuilder) {
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
    public <B extends ViewtonParamsBuilder> B page(int page) {
        params.put("page", String.valueOf(page));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B noPagination() {
        params.put(PAGE_SIZE_PARAM_NAME, "-1");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B pageSize(int pageSize) {
        params.put(PAGE_SIZE_PARAM_NAME, String.valueOf(pageSize));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B count() {
        params.put("count", "true");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B distinct() {
        params.put("distinct", "true");
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B total() {
        params.put("total", "true");
        return (B) this;
    }

    public <B extends ViewtonParamsBuilder> B totalAttributes(Function<B, List<FilterBuilder<?>>> attributes) {
        return paramWithFields(attributes, "totalAttributes");
    }

    public <B extends ViewtonParamsBuilder> B attributes(Function<B, List<FilterBuilder<?>>> attributes) {
        return paramWithFields(attributes, "attributes");
    }

    @SuppressWarnings("unchecked")
    private <B extends ViewtonParamsBuilder> B paramWithFields(Function<B, List<FilterBuilder<?>>> attributes, String paramName) {
        Assert.notNull(attributes, "Attributes should not be null");
        List<String> attributesNames = attributes.apply((B) this).stream().map(FilterBuilder::key).collect(Collectors.toList());
        if (!attributesNames.isEmpty()) {
            params.put(paramName, String.join(",", attributesNames));
        }

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B attributes(FilterBuilder<?>... attributes) {
        if (attributes.length > 0) {
            List<String> attributesNames = Arrays.stream(attributes).map(FilterBuilder::key).collect(Collectors.toList());
            params.put("attributes", String.join(",", attributesNames));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public <B extends ViewtonParamsBuilder> B totalAttributes(FilterBuilder<?>... totalAttributes) {
        if (totalAttributes.length > 0) {
            List<String> attributesNames = Arrays.stream(totalAttributes).map(FilterBuilder::key).collect(Collectors.toList());
            params.put("totalAttributes", String.join(",", attributesNames));
        }
        return (B) this;
    }

    public Map<String, String> build() {
        return params;
    }

    public static class FilterBuilder<B extends ViewtonParamsBuilder> {

        protected String fieldName;
        protected String value;
        protected B caller;

        public FilterBuilder(Object fieldName, B caller) {
            this.fieldName = fieldName.toString();
            this.caller = caller;
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
            caller.registerParam(this);
            return caller;
        }

        public B notEqualsTo(Object to) {
            this.value = new NotEqualOperator().getValue() + to.toString();
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

    public static class OrAndBuilder<B extends ViewtonParamsBuilder> {
        private final B caller;
        private final FilterBuilder<B> param;

        public OrAndBuilder(B caller, FilterBuilder<B> param) {
            this.caller = caller;
            this.param = param;
        }

        public B next() {
            caller.registerParam(param);
            return caller;
        }

        public OrAndBuilder<B> or(String value) {
            param.setValue(param.value() + new OrOperator().getValue() + value);
            return this;
        }
    }
}

