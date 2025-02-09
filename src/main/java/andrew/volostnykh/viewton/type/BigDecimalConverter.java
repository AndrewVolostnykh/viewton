package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

import java.math.BigDecimal;
import java.math.BigInteger;

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
