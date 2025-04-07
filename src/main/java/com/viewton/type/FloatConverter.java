package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;

public class FloatConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Float.class == javaType || float.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Float.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
