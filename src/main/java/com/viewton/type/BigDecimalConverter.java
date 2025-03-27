package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;

import java.math.BigDecimal;

public class BigDecimalConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return BigDecimal.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(new BigDecimal(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
