package com.viewton.type;

import com.viewton.ComparableValue;
import com.viewton.RawValue;

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
