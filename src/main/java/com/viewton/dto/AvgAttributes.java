package com.viewton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class AvgAttributes {

    private List<String> attributes;
    private List<String> groupByAttributes;

    public List<String> getAllFields() {
        if (groupByAttributes == null) {
            return attributes;
        }

        return Stream.concat(groupByAttributes.stream(), attributes.stream())
                .toList();
    }
}
