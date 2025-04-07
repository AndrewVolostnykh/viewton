package com.viewton.type;

import com.viewton.dto.ComparableValue;
import com.viewton.dto.RawValue;
import com.viewton.utils.DateUtil;

import java.time.LocalDate;

public class LocalDateConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return LocalDate.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(DateUtil.parseIsoDate(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
