package com.viewton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Container for parsed 'avg' expression.
 * Contains attributes avg value of which will be selected and
 * group by attributes to group by table's columns.
 */
@Data
@AllArgsConstructor
public class AvgAttributes implements AggregateAttributes {

    private List<String> attributes;
    private List<String> groupByAttributes;
}
