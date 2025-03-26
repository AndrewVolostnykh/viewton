package com.viewton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvgAttributes {

    private List<String> attributes;
    private List<String> groupByAttributes;
}
