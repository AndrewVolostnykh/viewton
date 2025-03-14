package com.viewton.type;

import com.viewton.ComparableValue;
import com.viewton.RawValue;

public class ShortConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Short.class == javaType || short.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Short.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
