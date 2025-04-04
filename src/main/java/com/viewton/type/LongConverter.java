package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;

public class LongConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Long.class == javaType || long.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Long.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
