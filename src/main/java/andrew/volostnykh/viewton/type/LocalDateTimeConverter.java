package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;
import andrew.volostnykh.viewton.utils.DateUtil;

import java.time.LocalDate;
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
