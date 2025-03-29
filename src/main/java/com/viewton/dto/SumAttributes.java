package com.viewton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SumAttributes implements AggregateAttributes {

    private List<String> attributes;
    private List<String> groupByAttributes;
}
