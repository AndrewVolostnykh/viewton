package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

public class BooleanConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Boolean.class == javaType || boolean.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(Boolean.valueOf(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
