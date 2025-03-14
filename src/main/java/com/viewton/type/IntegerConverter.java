package com.viewton.type;

import com.viewton.ComparableValue;
import com.viewton.RawValue;

public class IntegerConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Integer.class == javaType || int.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Integer.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
