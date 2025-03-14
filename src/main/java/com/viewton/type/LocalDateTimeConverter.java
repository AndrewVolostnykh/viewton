package com.viewton.type;

import com.viewton.ComparableValue;
import com.viewton.RawValue;
import com.viewton.utils.DateUtil;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return LocalDateTime.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(DateUtil.parseIsoDateTime(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
