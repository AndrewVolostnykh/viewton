package com.viewton.dto;

import com.viewton.utils.ViewtonReflections;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public interface AggregateAttributes {

    List<String> getAttributes();

    List<String> getGroupByAttributes();

    /**
     * Returns a list of avg attributes and group by attributes.
     * Using {@link ViewtonReflections} searches for aliases for specified attributes,
     * to remap incompatible returning type (in case of strict non-float type of attribute)
     * to relevant attribute with corresponding type.
     *
     * @param entityType is a java type of queried entity.
     * @return list of attributes with applied aliases.
     */
    default List<String> getAllFields(
            BiFunction<List<String>, Class<?>, List<String>> aggregateAliasesGetter,
            Class<?> entityType
    ) {
        List<String> attributes = aggregateAliasesGetter.apply(getAttributes(), entityType);
        if (getGroupByAttributes() == null) {
            return attributes;
        }

        return Stream.concat(getGroupByAttributes().stream(), attributes.stream())
                .toList();
    }
}
