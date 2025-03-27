package com.viewton.dto;

import com.viewton.utils.ViewtonReflections;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

/**
 * Container for parsed 'avg' expression.
 * Contains attributes avg value of which will be selected and
 * group by attributes to group by table's columns.
 */
@Data
@AllArgsConstructor
public class AvgAttributes {

    private List<String> attributes;
    private List<String> groupByAttributes;

    /**
     * Returns a list of avg attributes and group by attributes.
     * Using {@link ViewtonReflections} searches for aliases for specified attributes,
     * to remap incompatible returning type (in case of strict non-float type of attribute)
     * to relevant attribute with corresponding type.
     *
     * @param entityType is a java type of queried entity.
     * @return list of attributes with applied aliases.
     */
    public List<String> getAllFields(Class<?> entityType) {
        List<String> attributes = ViewtonReflections.getAvgAliases(this.attributes, entityType);
        if (groupByAttributes == null) {
            return attributes;
        }

        return Stream.concat(groupByAttributes.stream(), attributes.stream())
                .toList();
    }
}
