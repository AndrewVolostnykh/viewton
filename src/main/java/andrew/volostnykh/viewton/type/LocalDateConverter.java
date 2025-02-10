package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;
import andrew.volostnykh.viewton.utils.DateUtil;

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
