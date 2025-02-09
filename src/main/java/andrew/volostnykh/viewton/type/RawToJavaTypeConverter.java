package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

public interface RawToJavaTypeConverter {

    boolean requiredType(Class<?> javaType);

    ComparableValue convert(RawValue rawValue);
}
