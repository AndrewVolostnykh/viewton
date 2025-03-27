package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;

public class DoubleConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Double.class == javaType || double.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Double.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
