package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

import java.math.BigInteger;

public class BigIntegerConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return BigInteger.class == javaType;
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(new BigInteger(rawValue.getValue()), rawValue.isIgnoreCase());
    }
}
