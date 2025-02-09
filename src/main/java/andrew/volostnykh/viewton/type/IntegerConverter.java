package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

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
